FROM node:20-alpine

WORKDIR /app

# Install expo-cli and other tools
RUN npm install -g expo-cli

# Copy package files
COPY package*.json ./
RUN npm install

# Copy source
COPY . .

# Ports for Expo
EXPOSE 8081 19000 19001 19002

# The command to start expo. 
# Note: To connect from a real device, you usually need --tunnel or to set REACT_NATIVE_PACKAGER_HOSTNAME
CMD ["npx", "expo", "start"]
