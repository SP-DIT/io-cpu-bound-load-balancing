const express = require('express');
const app = express();

// IO-bound endpoint
app.get('/io-bound', async (req, res) => {
    const numRequests = 10;
    const promises = Array.from({ length: numRequests }, () => makeHttpRequest());
    await Promise.all(promises);
    res.send('All IO-bound requests completed.');
});

// CPU-bound endpoint
app.get('/cpu-bound', (req, res) => {
    let sum = 0;

    for (let i = 0; i < 100000; i++) {
        for (let j = 0; j < 100000; j++) {
            sum += i + j;
        }
    }

    res.send(`CPU-bound computation done. Sum: ${sum}`);
});

async function makeHttpRequest() {
    return new Promise((resolve, reject) => {
        setTimeout(() => resolve(), 3000);
    });
}

const port = process.env.PORT || 3000;
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
