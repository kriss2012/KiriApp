import axios from 'axios';
import dotenv from 'dotenv';
dotenv.config();

const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY;
const MODEL = "google/gemini-2.0-flash-001";

async function test() {
    console.log("Testing Gemini 1.5 connection...");
    console.log("Key:", OPENROUTER_API_KEY ? "Found" : "Missing");
    
    try {
        const response = await axios.post(
            "https://openrouter.ai/api/v1/chat/completions",
            {
                model: MODEL,
                messages: [{ role: "user", content: "Hello! Say 'Gemini 1.5 is active' if you can hear me." }],
                temperature: 0.7
            },
            {
                headers: {
                    "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
                    "Content-Type": "application/json"
                },
                timeout: 10000
            }
        );
        console.log("Response:", response.data.choices[0].message.content);
        console.log("STATUS: SUCCESS");
    } catch (e: any) {
        console.error("STATUS: FAILED");
        console.error("Error:", e.response?.data || e.message);
    }
}

test();
