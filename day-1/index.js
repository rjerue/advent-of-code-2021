const fs = require("fs");
const readline = require("readline");

const bigZero = BigInt(0);
function sumWindow(input) {
  return input.reduce((sum, n) => sum + n, bigZero);
}

async function processLineByLine(input, windowSize = 1) {
  const fileStream = fs.createReadStream(input);

  const rl = readline.createInterface({
    input: fileStream,
    crlfDelay: Infinity,
  });

  let previous;
  let increases = 0;
  const slidingWindow = [];

  for await (const line of rl) {
    const asNumber = BigInt(line);
    const buildingWindow = slidingWindow.length < windowSize;
    if (!buildingWindow) {
      // remove from end of window
      slidingWindow.pop();
    }
    // add to start of window
    slidingWindow.unshift(asNumber);
    const sum = sumWindow(slidingWindow);
    if (!buildingWindow && sum > previous) {
      increases++;
    }
    previous = sum;
  }
  return increases;
}

const input = process.argv[2];
const windowSize = process.argv[3];

if (!fs.existsSync(input)) {
  throw new Error("Input file does not exist!");
}

processLineByLine(input, parseInt(windowSize)).then((result) =>
  console.log(result, "increases")
);
