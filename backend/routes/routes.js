function generateId() {
  let result = '';
  const characters = '0123456789';
  const charactersLength = characters.length;
  for (let i = 0; i < 5; i++ ) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}

async function addGenre(roomId, genres, admin) {
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
}

async function getGenresInRoom(roomId, admin) {
  const db = admin.firestore();
  const room = db.collection('rooms').doc(roomId);
  const selectedRoom = await room.get();
  return selectedRoom.exists ? selectedRoom.data().genres.toString() : null;
}

async function createRoom(admin, token) {
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
}

async function joinRoom(admin, roomId, token) {
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
}

async function getTokens(admin, roomId) {
  const db = admin.firestore();
  const room = db.collection('rooms').doc(roomId);
  const selectedRoom = await room.get();
  return selectedRoom.exists ? selectedRoom.data().tokens : null;
}

async function likeMovie(admin, roomId, movieId) {
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

const appRouter = (app, fs, admin) => {

  app.post("/create", (req, res) => {
    const token = req.headers['token'];
    createRoom(admin, token).then(r => {
      res.status(200).json({room :r})
    })
  });

  app.post("/join/:roomId", (req, res) => {
    const roomId = req.params["roomId"];
    const token = req.headers["token"];
    joinRoom(admin, roomId, token).then(r => {
      if(r === 200)
        res.status(200).send({info: 'OK'})
      else
        res.status(400).send({info: 'Room does not exist'})
    })
  });

  app.post("/:roomId/genres", (req, res) => {
    const list = req.query.genre.map(i => parseInt(i));
    const roomId = req.params["roomId"];
    addGenre(roomId, list, admin).then(r => {
      if(r === 200)
        res.status(200).send({info: 'OK'})
      else
        res.status(400).send({info: 'Room does not exist'})
    })
  });

  app.get("/:roomId/genres", (req, res) => {
    const roomId = req.params["roomId"];
    const genresInRoom = getGenresInRoom(roomId, admin);
    genresInRoom.then(genres => {
      if (genres)
        res.status(200).send({genres: genres})
    });
  });

  app.post("/:roomId/like/:movieId", (req, res) => {
    const roomId = req.params["roomId"];
    const movieId = req.params["movieId"];
    likeMovie(admin, roomId, movieId).then(r => {
      if (r) {
        getTokens(admin, roomId).then(r => {
          const message = {
            data: {movieId: movieId},
            tokens: r
          };
          admin.messaging().sendMulticast(message).then(() => console.log("Match was sent"))
        })
      }
      res.status(200).send({match: r});
    });
  });

};

module.exports = appRouter;
