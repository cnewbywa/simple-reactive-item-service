db = db.getSiblingDB("items");
 
db.createCollection("item");

db.item.createIndex({ itemId: -1 });

db = db.getSiblingDB("admin");

db.createUser({
	user: "item",
	pwd: process.env.MONGO_INITDB_PASSWORD,
	roles: [{ role: "readWrite", db: "items" }]
});
