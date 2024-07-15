const WorkloadConfig = {
  test: [
    { duration: "30s", target: getTarget(30) },
    { duration: "30s", target: getTarget(30) },
    { duration: "30s", target: getTarget(0) },
  ],
  average: [
    { duration: "1m", target: getTarget(200) },
    { duration: "3m", target: getTarget(200) },
    { duration: "1m", target: getTarget(0) },
  ],
  stress: [
    { duration: "1m", target: getTarget(400) },
    { duration: "3m", target: getTarget(400) },
    { duration: "1m", target: getTarget(0) },
  ],
  scalability: [
    { duration: "1m", target: getTarget(256) },
    { duration: "3m", target: getTarget(256) },
    { duration: "1m", target: getTarget(512) },
    { duration: "3m", target: getTarget(512) },
    { duration: "1m", target: getTarget(1024) },
    { duration: "3m", target: getTarget(1024) },
    { duration: "1m", target: getTarget(2048) },
    { duration: "3m", target: getTarget(2048) },
    { duration: "1m", target: getTarget(0) },
  ],
  breakpoint: [
    {
      duration: "5m",
      target: 10000,
    },
  ],
};

function getTarget(defaultValue) {
  let val;
  try {
    if (__ENV.USER) {
      val = parseInt(__ENV.USER);
    }
    if (isNaN(val) || val <= 0) {
      val = defaultValue;
    }
  } catch (err) {
    val = defaultValue
  }
  return val;
}

const stages = WorkloadConfig[__ENV.WORKLOAD] || WorkloadConfig.average;
export const options = {
  stages: stages,
};
