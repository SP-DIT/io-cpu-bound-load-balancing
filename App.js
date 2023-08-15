const express = require('express');
const app = express();

const numberOfConcurrentRequest = 10;
const delay = 3000; //ms
const loopCount = 100000;

// IO-bound endpoint
app.get('/io-bound', async (req, res) => {
    const promises = Array.from({ length: numberOfConcurrentRequest }, () => makeHttpRequest());
    await Promise.all(promises);
    res.send('All IO-bound requests completed.');
});

async function makeHttpRequest() {
    return new Promise((resolve, reject) => {
        setTimeout(() => resolve(), delay);
    });
}

// CPU-bound endpoint
app.get('/cpu-bound', (req, res) => {
    let sum = 0;

    for (let i = 0; i < loopCount; i++) {
        for (let j = 0; j < loopCount; j++) {
            sum += i + j;
        }
    }

    res.send(`CPU-bound computation done. Sum: ${sum}`);
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
