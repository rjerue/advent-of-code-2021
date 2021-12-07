const fs = require("fs");

const input = fs.readFileSync(process.argv[2] || "./example.txt").toString();
const days = parseInt(process.argv[3]) || 80;

const nums = input
  .substring(0, input.length - 1)
  .split(",")
  .map((n) => parseInt(n));

const buckets = Object.values(
  nums.reduce(
    (list, n) => ({
      ...list,
      [n]: list[n] + 1,
    }),
    new Array(9).fill(0).reduce((o, _, i) => ({ ...o, [i]: 0 }), {})
  )
);
for (let day = 0; day < days; day++) {
  const newFish = buckets.shift();
  buckets[6] += newFish;
  buckets.push(newFish);
}

const answer = buckets.reduce((sum, x) => sum + x);

console.log(answer);
