const functions = require("firebase-functions");
const admin = require("firebase-admin");
const sgMail = require("@sendgrid/mail");

admin.initializeApp();

sgMail.setApiKey(functions.config().sendgrid.key);

exports.sendJobNotification = functions.firestore
    .document("jobs/{jobId}")
    .onCreate(async (snap, context) => {
        const job = snap.data();
        const jobTitle = job.jobTitle;
        const company = job.companyName;

        try {
            // Fetch all alumni users
            const usersSnapshot = await admin.firestore()
                .collection("users")
                .where("role", "==", "alumni")
                .get();

            if (usersSnapshot.empty) {
                console.log("No alumni found.");
                return;
            }

            const emails = usersSnapshot.docs.map(doc => doc.data().email);

            const msg = {
                to: emails,
                from: "gideonmwangi718@gmail.com",
                subject: `New Job Posted: ${jobTitle}`,
                html: `
                    <h2>🎉 New Job Alert!</h2>
                    <p><strong>${jobTitle}</strong> at <strong>${company}</strong> has been posted.</p>
                    <p>Open the Manyura Job Portal to view more details and apply.</p>
                `
            };

            await sgMail.sendMultiple(msg);
            console.log("Emails sent successfully!");

        } catch (error) {
            console.error("Error sending email:", error);
        }
    });
