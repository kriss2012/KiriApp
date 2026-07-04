import prisma from '../utils/prisma.js';
/**
 * Controller for APEX AI Launchpad (AAL) activities and onboarding.
 */
export const getOnboarding = async (req, res) => {
    try {
        const userId = req.params['userId'];
        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        // Check if user exists first to be strictly correct
        const user = await prisma.user.findUnique({ where: { id: userId } });
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const onboarding = await prisma.aalOnboarding.findUnique({
            where: { userId }
        });
        // Instead of 404, return 200 with null if not onboarded yet
        // This avoids throwing exceptions in Android Retrofit client
        res.status(200).json(onboarding || null);
    }
    catch (error) {
        console.error(`[AAL] Error fetching onboarding for ${req.params['userId']}:`, error);
        res.status(500).json({ message: 'Error fetching onboarding', error: error.message });
    }
};
export const updateMindsetScore = async (req, res) => {
    try {
        const { userId, mindsetScore } = req.body;
        const onboarding = await prisma.aalOnboarding.upsert({
            where: { userId },
            update: { mindsetScore },
            create: { userId, mindsetScore }
        });
        res.status(200).json(onboarding);
    }
    catch (error) {
        res.status(500).json({ message: 'Error updating mindset score', error: error.message });
    }
};
export const submitActivity = async (req, res) => {
    try {
        const { userId, activityNumber, submissionUrl } = req.body;
        const activity = await prisma.aalActivity.create({
            data: {
                userId,
                activityNumber,
                submissionUrl,
                status: 'SUBMITTED'
            }
        });
        res.status(201).json(activity);
    }
    catch (error) {
        res.status(500).json({ message: 'Error submitting activity', error: error.message });
    }
};
export const getActivities = async (req, res) => {
    try {
        const userId = req.params['userId'];
        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        const activities = await prisma.aalActivity.findMany({
            where: { userId },
            orderBy: { activityNumber: 'asc' }
        });
        res.status(200).json(activities);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching activities', error: error.message });
    }
};
export const verifyActivity = async (req, res) => {
    try {
        const activityId = req.params['activityId'];
        if (!activityId) {
            return res.status(400).json({ message: 'Activity ID is required' });
        }
        const activity = await prisma.aalActivity.update({
            where: { id: activityId },
            data: { status: 'VERIFIED' }
        });
        res.status(200).json(activity);
    }
    catch (error) {
        res.status(500).json({ message: 'Error verifying activity', error: error.message });
    }
};
export const getInstitutions = async (req, res) => {
    try {
        const institutions = await prisma.institution.findMany();
        const formatted = institutions.map(inst => ({
            institution_id: inst.id,
            name: inst.name,
            spoc_user_id: inst.spocUserId
        }));
        res.status(200).json(formatted);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching institutions', error: error.message });
    }
};
export const registerForEvent = async (req, res) => {
    try {
        const { eventId, userId, formData } = req.body;
        if (!eventId || !userId) {
            return res.status(400).json({ message: 'Event ID and User ID are required' });
        }
        // Resilience: handle both stringified and object form data
        let parsedData = formData;
        if (typeof formData === 'string') {
            try {
                parsedData = JSON.parse(formData);
            }
            catch (e) {
                parsedData = { raw: formData };
            }
        }
        const registration = await prisma.eventRegistration.create({
            data: {
                eventId,
                userId,
                status: 'REGISTERED',
                formData: parsedData
            }
        });
        res.status(201).json({
            registration_id: registration.id,
            event_id: registration.eventId,
            user_id: registration.userId,
            _status: 'REGISTERED',
            form_data: parsedData,
            qr_scanned_at: null
        });
    }
    catch (error) {
        console.error('[AAL] Registration Error:', error);
        res.status(500).json({ message: 'Error registering for event', error: error.message });
    }
};
export const submitLiveInput = async (req, res) => {
    try {
        const { userId, format_type, contentUrl, _context } = req.body;
        res.status(201).json({
            input_id: `live_${Date.now()}`,
            user_id: userId || 'anonymous',
            format_type: format_type || 'TEXT',
            content_url: contentUrl || null,
            _context: _context || 'DAY_TO_DAY',
            ai_processed: true
        });
    }
    catch (error) {
        res.status(500).json({ message: 'Error submitting live input', error: error.message });
    }
};
export const getAiMatches = async (req, res) => {
    try {
        const userId = req.params['userId'];
        const mentors = await prisma.user.findMany({
            where: {
                stakeholderRoles: {
                    some: {
                        roleName: 'MENTOR'
                    }
                }
            },
            take: 2
        });
        const matches = mentors.map((m) => ({
            match_id: `match_${userId}_${m.id}`,
            source_user_id: userId,
            target_user_id: m.id,
            match_reason: `Highly aligned match based on interest in ${m.department || 'innovation'}.`,
            _status: 'SUGGESTED'
        }));
        res.status(200).json(matches);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching matches', error: error.message });
    }
};
//# sourceMappingURL=aalController.js.map