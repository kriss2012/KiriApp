import axios from 'axios';
async function testProduction() {
    const baseUrl = "https://asgapp.onrender.com/api/";
    const email = `test_prod_user_${Date.now()}@example.com`;
    const password = "password123";
    const fullName = "Production Test User";
    console.log(`1. Registering test user on production: ${email}...`);
    let token = "";
    let userId = "";
    try {
        const registerRes = await axios.post(`${baseUrl}auth/register`, {
            email,
            password,
            fullName,
            userCategory: "STUDENT"
        });
        token = registerRes.data.token;
        userId = registerRes.data.user.id;
        console.log(`Registration successful! Token: ${token.substring(0, 10)}... UserID: ${userId}`);
    }
    catch (err) {
        console.error("Registration failed:", err.response?.data || err.message);
        return;
    }
    const authHeaders = {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };
    // List of endpoints to test
    const endpoints = [
        {
            name: "Fetch Profile",
            fn: () => axios.get(`${baseUrl}users/profile/${userId}`, authHeaders)
        },
        {
            name: "Update Profile",
            fn: () => axios.put(`${baseUrl}users/profile/${userId}`, {
                fullName: "Production Test User Updated",
                role: "STUDENT",
                bio: "Bio Updated",
                department: "CSE",
                college: "ABC College",
                year: "3rd",
                phoneNumber: `+123456789${Math.floor(Math.random() * 10)}`,
                website: "https://example.com",
                githubUrl: "https://github.com",
                linkedInUrl: "https://linkedin.com",
                services: ["Coding", "Design"]
            }, authHeaders)
        },
        {
            name: "Fetch Events",
            fn: () => axios.get(`${baseUrl}events`, authHeaders)
        },
        {
            name: "Fetch Notifications",
            fn: () => axios.get(`${baseUrl}notifications/${userId}`, authHeaders)
        },
        {
            name: "Fetch Connections",
            fn: () => axios.get(`${baseUrl}connections/list/${userId}`, authHeaders)
        },
        {
            name: "Fetch Pitches",
            fn: () => axios.get(`${baseUrl}pitches`, authHeaders)
        },
        {
            name: "Send AI Chat",
            fn: () => axios.post(`${baseUrl}ai/chat`, { content: "hello" }, authHeaders)
        }
    ];
    for (const endpoint of endpoints) {
        console.log(`\nTesting ${endpoint.name}...`);
        try {
            const res = await endpoint.fn();
            console.log(`✅ ${endpoint.name} Success! Status: ${res.status}`);
        }
        catch (err) {
            console.error(`❌ ${endpoint.name} Failed! Status: ${err.response?.status}`);
            console.error(`Error details:`, JSON.stringify(err.response?.data || {}, null, 2));
        }
    }
}
testProduction();
//# sourceMappingURL=test-production.js.map