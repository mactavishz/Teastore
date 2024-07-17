import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import math
import os
import sys
from tabulate import tabulate


def calculate_duration_stats(df, api_name):
    durations = df[df["metric_name"] == "http_req_duration"][["metric_value"]]
    stats = {
        "API": api_name,
        "Max": durations.max()["metric_value"],
        "Min": durations.min()["metric_value"],
        "Avg": durations.mean()["metric_value"],
        "p95": np.percentile(durations, 95),
    }
    return stats


def print_duration_stats_table(duration_stats, service_name, load_type):
    table_data = [
        (
            stats["API"],
            f"{stats['Max']:.2f}",
            f"{stats['Min']:.2f}",
            f"{stats['Avg']:.2f}",
            f"{stats['p95']:.2f}",
        )
        for stats in duration_stats
    ]
    headers = ["API", "Max (ms)", "Min (ms)", "Avg (ms)", "p95 (ms)"]
    table = tabulate(table_data, headers=headers, tablefmt="grid")
    print(f"\nDuration Statistics for {service_name} - {load_type}:")
    print(table)


def load_and_process_csv(file_path):
    dtypes = {
        "metric_name": "str",
        "timestamp": "int64",
        "metric_value": "float64",
    }
    df = pd.read_csv(file_path, dtype=dtypes, low_memory=False)
    df["timestamp"] = pd.to_datetime(df["timestamp"], unit="s")
    start_time = df["timestamp"].min()
    df["time_from_start"] = (df["timestamp"] - start_time).dt.total_seconds()
    return df


def process_metrics(df):
    metrics_df = df[df["metric_name"].isin(["http_req_duration", "vus"])]
    metrics_df_1 = (
        metrics_df.groupby(["time_from_start", "metric_name"])["metric_value"]
        .mean()
        .unstack()
    )
    result_1 = metrics_df_1.reset_index()
    metrics_df_2 = (
        metrics_df.groupby(["time_from_start", "metric_name"])["metric_value"]
        .quantile(0.95)
        .unstack()
    )
    result_2 = metrics_df_2.reset_index()
    return result_1, result_2


def plot_metrics(dfs, api_name, output_path):
    avg_df, p95_df = dfs
    apis = avg_df["api"].unique()
    n_apis = len(apis)
    # Determine the grid layout
    if n_apis <= 3:
        n_rows, n_cols = n_apis, 1
    else:
        n_rows = math.ceil(n_apis / 2)
        n_cols = 2
    fig, axs = plt.subplots(
        n_rows, n_cols, figsize=(15 * n_cols, 6 * n_rows), squeeze=False
    )
    for i, api in enumerate(apis):
        row = i // n_cols
        col = i % n_cols
        ax1 = axs[row, col]
        api_avg_data = avg_df[avg_df["api"] == api]
        api_p95_data = p95_df[p95_df["api"] == api]
        # Plot duration
        if "http_req_duration" in api_avg_data.columns:
            color = f"C{i}"
            ax1.fill_between(
                api_avg_data["time_from_start"],
                api_avg_data["http_req_duration"],
                alpha=0.3,
                color=color,
                label="Avg",
            )
            ax1.plot(
                api_avg_data["time_from_start"],
                api_avg_data["http_req_duration"],
                color=color,
                linewidth=1,
            )

        if "http_req_duration" in api_p95_data.columns:
            color = f"C{i + 1}"
            ax1.fill_between(
                api_p95_data["time_from_start"],
                api_p95_data["http_req_duration"],
                alpha=0.3,
                color=color,
                label="P95",
            )
            ax1.plot(
                api_p95_data["time_from_start"],
                api_p95_data["http_req_duration"],
                color=color,
                linewidth=1,
            )
        # Plot failures if any
        # if (
        #     "http_req_failed" in api_avg_data.columns
        #     and api_avg_data["http_req_failed"].sum() > 0
        # ):
        #     ax1.plot(
        #         api_avg_data["time_from_start"],
        #         api_avg_data["http_req_failed"],
        #         label="Failures",
        #         linestyle=":",
        #         color="red",
        #         linewidth=2,
        #     )
        # Plot VUs
        ax2 = ax1.twinx()
        if "vus" in api_avg_data.columns:
            ax2.plot(
                api_avg_data["time_from_start"],
                api_avg_data["vus"],
                color="tab:green",
                label="VUs",
                linewidth=1,
            )
        # Set labels and title
        ax1.set_xlabel("Time from start (seconds)")
        ax1.set_ylabel("Duration (ms)", color="tab:blue")
        ax2.set_ylabel("Number of VUs", color="tab:green")
        ax1.set_title(f"{api}")
        # Set y-axis limits
        ax1.set_ylim(bottom=0)
        ax2.set_ylim(bottom=0)
        # Add legend
        lines1, labels1 = ax1.get_legend_handles_labels()
        lines2, labels2 = ax2.get_legend_handles_labels()
        ax1.legend(
            lines1 + lines2,
            labels1 + labels2,
            loc="upper left",
            bbox_to_anchor=(0, 1.15),
            ncol=3,
        )
    # Remove empty subplots
    for j in range(i + 1, n_rows * n_cols):
        fig.delaxes(axs.flatten()[j])
    plt.suptitle(f"HTTP Duration - {api_name}", fontsize=16, y=1.02)
    plt.tight_layout()
    plt.savefig(output_path, bbox_inches="tight", dpi=300)
    plt.close()


def process_service(service_path, service_name):
    for load_type in ["average", "stress", "breakpoint"]:
        load_type_path = os.path.join(service_path, load_type)
        if not os.path.exists(load_type_path):
            continue

        all_avg_metrics = []
        all_p95_metrics = []
        api_duration_stats = []
        for csv_file in os.listdir(load_type_path):
            if csv_file.endswith(".csv"):
                file_path = os.path.join(load_type_path, csv_file)
                df = load_and_process_csv(file_path)
                api_name = csv_file.replace(".csv", "")
                # Calculate and print duration statistics
                duration_stats = calculate_duration_stats(df, api_name)
                api_duration_stats.append(duration_stats)
                avg_df, p95_df = process_metrics(df)
                avg_df["api"] = api_name
                p95_df["api"] = api_name
                all_avg_metrics.append(avg_df)
                all_p95_metrics.append(p95_df)
        print_duration_stats_table(api_duration_stats, service_name, load_type)
        combined_avg_metrics = pd.concat(all_avg_metrics)
        compined_p95_metrics = pd.concat(all_p95_metrics)
        output_path = os.path.join(
            load_type_path, f"{service_name}_{load_type}_metrics.png"
        )
        plot_metrics(
            (combined_avg_metrics, compined_p95_metrics),
            f"{service_name} - {load_type}",
            output_path,
        )


if __name__ == "__main__":
    # get the base path from the argument, if not present use the default path
    default_base_path = os.path.join(os.getcwd(), "reports")
    base_path = sys.argv[1] if len(sys.argv) > 1 else default_base_path
    print(f"Processing reports from: {base_path}")
    # Main execution
    services = ["auth", "image", "persistence", "recommender", "webui"]
    print("Processing reports for the following services: " + ", ".join(services))

    for service in services:
        service_path = os.path.join(base_path, service)
        if os.path.exists(service_path):
            process_service(service_path, service)

    print("Processing complete. Check the respective folders for the generated graphs.")
