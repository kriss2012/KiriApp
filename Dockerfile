# Fail-safe Dockerfile for Repository Root
FROM node:18

WORKDIR /app

# Copy the backend folder contents to the container root
COPY backend/package*.json ./
COPY backend/prisma ./prisma/
COPY backend/global-bundle.pem ./

# Install dependencies
RUN npm install

# Copy the rest of the backend source
COPY backend/ .

# Generate Prisma Client
RUN npx prisma generate

# Build TypeScript
RUN npm run build

# Expose the API port
EXPOSE 5000

# Start the application
CMD ["npm", "start"]
