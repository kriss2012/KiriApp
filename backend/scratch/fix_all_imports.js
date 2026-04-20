import fs from 'fs';
import path from 'path';

const rootDir = 'c:/Users/krishna/AndroidStudioProjects/ASGApp/app/src/main/java/com/apex/asg';

const models = [
  'UserDto', 'EventDto', 'JobDto', 'NotificationDto', 'ConnectionDto', 'PitchDto', 'MessageDto',
  'AuthResponse', 'LoginRequest', 'RegisterRequest', 'UserResponse', 'CreateEventRequest',
  'CreateJobRequest', 'CreatePitchRequest', 'AiMessageRequest', 'AiMessageResponse',
  'ActivityDto', 'UserStatsDto', 'MentorSessionDto', 'HeatMapDto', 'ProjectArtifactDto',
  'InviteCodeDto', 'GenerateInviteRequest', 'MentorSessionRequest', 'HeatMapCountDto',
  'InvestorPitchDto', 'MarketTrendDto', 'MatchSuggestionDto', 'PitchCountDto'
];

function walk(dir) {
    const files = fs.readdirSync(dir);
    files.forEach(file => {
        const filePath = path.join(dir, file);
        const stats = fs.statSync(filePath);
        if (stats.isDirectory()) {
            walk(filePath);
        } else if (filePath.endsWith('.kt')) {
            fixFile(filePath);
        }
    });
}

function fixFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    let changed = false;

    models.forEach(model => {
        const oldImport = `import com.apex.asg.data.remote.${model}`;
        const newImport = `import com.apex.asg.data.remote.models.${model}`;
        
        if (content.includes(oldImport)) {
            content = content.replace(oldImport, newImport);
            changed = true;
        }
    });

    if (changed) {
        fs.writeFileSync(filePath, content, 'utf8');
        console.log(`Fixed: ${filePath}`);
    }
}

walk(rootDir);
console.log('Bulk import fix complete.');
