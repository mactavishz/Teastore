# TeaStore - Runnable Refactored System

This branch contains the runnable refactored system for the TeaStore project.

Check out the [REPRODUCTION.md](/REPRODUCTION.md) to see how to develop, deploy and reproduce the evaluation.

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [JDK 17](https://openjdk.org/projects/jdk/17), using [SDKMAN](https://sdkman.io/) is recommended
- [Maven](https://maven.apache.org/download.cgi)

## Quick Start

1. Build the databases and the static images

In the root directory of the project, run the following command:

```bash
cd tools && ./build.sh
```

This will build the databases in **3 sizes** and the static images correspond to different database sizes for the TeaStore project.

2. Create the network for the TeaStore project

In the root directory of the project, run the following command:

```bash
# this is only needed once
docker network create teastore-network
```

3. Build the docker images for the TeaStore project

In the root directory of the project, run the following command:

```bash
# default size is "mid"
docker compose build

# or specify the size: "small", "mid", "large"
docker compose build --build-arg DB_SIZE=small
```

4. Start the TeaStore project

In the root directory of the project, run the following command:

```bash
docker compose up -d
```

5. Access the Web Interface

Go to [http://localhost:8080/tools.descartes.teastore.webui/](http://localhost:8080/tools.descartes.teastore.webui/) to access the web interface of the TeaStore project.
