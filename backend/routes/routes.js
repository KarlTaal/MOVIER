const service = require('./service')

const appRouter = (app, fs, admin) => {

  app.get("/", (req, res) => {
    res.send("EVERYTHING WORKS")
  })

  app.post("/create", (req, res) => {
    const token = req.headers['token'];
    service.createRoom(admin, token).then(r => {
      res.status(200).json({room :r})
    })
  });

  app.post("/join/:roomId", (req, res) => {
    const roomId = req.params["roomId"];
    const token = req.headers["token"];
    service.joinRoom(admin, roomId, token).then(r => {
      if(r === 200)
        res.status(200).send({info: true})
      else
        res.status(400).send({info: false})
    })
  });

  app.post("/:roomId/genres", (req, res) => {
    const list = req.query.genre.map(i => parseInt(i));
    const roomId = req.params["roomId"];
    service.addGenre(roomId, list, admin).then(r => {
      if(r === 200)
        res.status(200).send({info: 'OK'})
      else
        res.status(400).send({info: 'Room does not exist'})
    })
  });

  app.get("/:roomId/genres", (req, res) => {
    const roomId = req.params["roomId"];
    const genresInRoom = service.getGenresInRoom(roomId, admin);
    genresInRoom.then(genres => {
      if (genres)
        res.status(200).send({genres: genres})
    });
  });

  app.post("/:roomId/like/:movieId", (req, res) => {
    const roomId = req.params["roomId"];
    const movieId = req.params["movieId"];
    service.likeMovie(admin, roomId, movieId).then(match => {
      if (match) {
        service.getTokens(admin, roomId).then(token => {
          const message = {
            data: {movieId: movieId},
            tokens: token
          };
          admin.messaging().sendMulticast(message).then(() => console.log("Match was sent"))
        })
      }
      res.status(200).send({match: match});
    });
  });

};

module.exports = appRouter;
