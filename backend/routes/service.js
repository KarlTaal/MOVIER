function generateId() {
  let result = '';
  const characters = '0123456789';
  const charactersLength = characters.length;
  for (let i = 0; i < 5; i++ ) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}

module.exports = {

  async addGenre(roomId, genres, admin) {
    const db = admin.firestore();
    const room = db.collection('rooms').doc(roomId);
    const selectedRoom = await room.get();
    if (selectedRoom.exists) {
      for(const genre of genres){
        await room.update({
          'genres': admin.firestore.FieldValue.arrayUnion(genre)
        })
      }
      return 200;
    } else {
      return 400;
    }
  },

  async getGenresInRoom(roomId, admin) {
    const db = admin.firestore();
    const room = db.collection('rooms').doc(roomId);
    const selectedRoom = await room.get();
    return selectedRoom.exists ? selectedRoom.data().genres.toString() : null;
  },

  async createRoom(admin, token) {
    const db = admin.firestore();
    let roomId = generateId();
    let room = db.collection('rooms').doc(roomId);
    let createdRoom = await room.get();
    while (createdRoom.exists) {
      roomId = generateId();
      room = db.collection('rooms').doc(roomId);
      createdRoom = await room.get();
    }
    await room.set({
        'userCount': 1,
        'genres': null,
        'likes': {},
        'tokens': [token]
      }
    );
    return roomId;
  },

  async joinRoom(admin, roomId, token) {
    const db = admin.firestore();
    let room = db.collection('rooms').doc(roomId);
    let joinRoom = await room.get();
    if (joinRoom.exists) {
      await room.update({
        userCount: admin.firestore.FieldValue.increment(1),
        tokens: admin.firestore.FieldValue.arrayUnion(token)
      });
      return 200;
    }
    return 400;
  },

  async getTokens(admin, roomId) {
    const db = admin.firestore();
    const room = db.collection('rooms').doc(roomId);
    const selectedRoom = await room.get();
    return selectedRoom.exists ? selectedRoom.data().tokens : null;
  },

  async likeMovie(admin, roomId, movieId) {
    const db = admin.firestore();
    let room = db.collection('rooms').doc(roomId)
    const selectedRoom = await room.get();
    if (selectedRoom.exists) {
      const likes = selectedRoom.data().likes;
      const userCount = selectedRoom.data().userCount;
      likes[movieId] = (likes[movieId] || 0) + 1;
      await room.update({
        'likes': likes
      })
      return likes[movieId] === userCount;
    }
    return null;
  }
}
