const fs = require("fs");
const readline = require("readline");

async function processLineByLine(input) {
  const fileStream = fs.createReadStream(input);

  const rl = readline.createInterface({
    input: fileStream,
    crlfDelay: Infinity,
  });

  let previous = null;
  let increases = 0;

  for await (const line of rl) {
    const asNumber = BigInt(line);
    if (asNumber > previous && previous !== null) {
      increases++;
    }
    previous = asNumber;
  }
  return increases;
}

const input = process.argv[2];

if (!fs.existsSync(input)) {
  throw new Error("Input file does not exist!");
}

processLineByLine(input).then((result) => console.log(result, "increases"));
