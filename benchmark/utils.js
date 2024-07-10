import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export function getRandomItems(arr, numItems) {
  // Copy the array to avoid modifying the original array
  let arrayCopy = arr.slice();
  
  // Fisher-Yates shuffle algorithm
  for (let i = arrayCopy.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [arrayCopy[i], arrayCopy[j]] = [arrayCopy[j], arrayCopy[i]];
  }
  
  // Return the first `numItems` items from the shuffled array
  return arrayCopy.slice(0, numItems);
}

// flip a coin and return true or false
export function choice() {
  return randomIntBetween(0, 1) == 1;
}

export function randomInt(min, max) {
  return randomIntBetween(min, max);
}