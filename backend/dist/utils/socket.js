import { Server } from 'socket.io';
import { Server as HttpServer } from 'http';
let io;
export const initSocket = (httpServer) => {
    io = new Server(httpServer, {
        cors: {
            origin: "*",
            methods: ["GET", "POST"]
        }
    });
    io.on('connection', (socket) => {
        console.log('A user connected:', socket.id);
        socket.on('join_room', (roomId) => {
            socket.join(roomId);
            console.log(`User ${socket.id} joined room ${roomId}`);
        });
        socket.on('send_message', (data) => {
            // data: { roomId, senderId, receiverId, content }
            io.to(data.roomId).emit('receive_message', data);
        });
        socket.on('disconnect', () => {
            console.log('User disconnected');
        });
    });
    return io;
};
export const getIO = () => {
    if (!io) {
        throw new Error('Socket.io not initialized');
    }
    return io;
};
export const emitToUser = (userId, event, data) => {
    if (io) {
        io.to(`user_${userId}`).emit(event, data);
        console.log(`[Socket] Emitted event '${event}' to room 'user_${userId}'`);
    }
    else {
        console.warn(`[Socket] Warning: io not initialized. Event '${event}' not sent to user_${userId}.`);
    }
};
//# sourceMappingURL=socket.js.map