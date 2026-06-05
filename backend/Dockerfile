# Use official Node.js image
FROM node:20

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install --ignore-scripts

# Copy the rest of the code
COPY . .

# Generate Prisma Client
# DATABASE_URL is not needed for generate (no DB connection), but prisma.config.js
# needs it defined. We pass a dummy value here; the real URL is set by Render at runtime.
RUN DATABASE_URL=postgresql://build:build@localhost:5432/build npx prisma generate

# Build TypeScript
RUN npm run build

# Expose port
EXPOSE 5000

# Start the application
CMD ["npm", "start"]
