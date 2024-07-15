const process = require('process');
const opentelemetry = require('@opentelemetry/sdk-node');
const {NodeTracerProvider} = require('@opentelemetry/sdk-trace-node');
const {getNodeAutoInstrumentations} = require('@opentelemetry/auto-instrumentations-node');
const {PrometheusExporter} = require('@opentelemetry/exporter-prometheus');
const {registerInstrumentations} = require('@opentelemetry/instrumentation');

// Create and register the NodeTracerProvider
const provider = new NodeTracerProvider();
provider.register();

// Register specific instrumentations
registerInstrumentations({
    instrumentations: [],
});

const sdk = new opentelemetry.NodeSDK({
    traceExporter: new opentelemetry.tracing.ConsoleSpanExporter(),
    instrumentations: [getNodeAutoInstrumentations()],
    metricReader: new PrometheusExporter({
        port: 9464, // the port where metrics will be exposed
    }),
});

sdk.start();

// gracefully shut down the SDK on process exit
process.on('SIGTERM', () => {
    sdk.shutdown()
        .then(() => console.log('Tracing terminated'))
        .catch((error) => console.log('Error terminating tracing', error))
        .finally(() => process.exit(0));
});