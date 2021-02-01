const { OAuth2Client } = require("google-auth-library");
const client = new OAuth2Client(process.env.CLIENT_ID, process.env.CLIENT_SECRET);
const express = require('express');
const app = express();
const port = 8080;

app.use(express.json());

app.get('/', (req, res) => {
    res.send("Hello World!")
});

app.post("/verify", async (req, res) => {
    console.log("Request received");
    console.log(req.body);
    const { token } = req.body;
    try {
        const verifiedUser = await verifyWithGoogle(token);

        // This is where you would check in your db if the user exists.
        // If they exist you can return some more info on them.
        // Otherwise, you can add them to the backend and request more info from the user if desired.

        res.json({ status: "success" })
    } catch (error) {
        console.log(error);
        res.json({ status: "failure" })
    }
});

app.listen(port, () => {
    console.log(`Listening on port ${port}`);
});

async function verifyWithGoogle(token) {
  const ticket = await client.verifyIdToken({
    idToken: token,
    audience: process.env.CLIENT_ID,
  });
  const payload = ticket.getPayload();
  const userId = payload["sub"];
  return { userId: userId };
}