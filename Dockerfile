# Fail-safe Dockerfile for Repository Root
FROM node:20

WORKDIR /app

# Copy the backend folder contents to the container root
COPY backend/package*.json ./
COPY backend/prisma ./prisma/
COPY backend/global-bundle.pem ./

# Install dependencies
# Install dependencies (ignore postinstall for now as schema isn't copied)
RUN npm install --ignore-scripts

# Copy the rest of the backend source
COPY backend/ .

# Generate Prisma Client (now that schema is copied)
RUN npx prisma generate

# Build TypeScript
RUN npm run build

# Expose the API port
EXPOSE 5000

# Start the application
CMD ["npm", "start"]
