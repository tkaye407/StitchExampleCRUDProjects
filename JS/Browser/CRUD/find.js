function findAll() {
 itemsCollection.find({}).asArray().then(results => {
   console.log(results);
 })
}

function findOne() {
	const filterDoc = {quantity: {$gte: 25}};

	itemsCollection.findOne().then(result => {
		if (result) {
			console.log("Successfully Found Document: " + JSON.stringify(result));
		} else {
			console.log("No matching document found");
		}
	}, (error) => {
		alert("Error Finding Document: " + error);
	});
}

function findOneAndReplace() {
	const filterDoc = {quantity: {$gte: 25}};

	itemsCollection.findOneAndReplace({}, {}).then(result => {
		if (result) {
			console.log("Successfully Found Document: " + JSON.stringify(result));
		} else {
			console.log("No matching document found");
		}
	}, (error) => {
		alert("Error Finding Document: " + error);
	});
}

function findOneById() {
	const filterDoc = {_id: new stitch.BSON.ObjectId("5c6331498b247b872c23d512")};

	itemsCollection.find(filterDoc, {limit: 1}).first().then(result => {
		if (result) {
			console.log("Successfully Found Document: " + JSON.stringify(result));
		} else {
			console.log("No matching document found");
		}
	}, (error) => {
		alert("Error Finding Document: " + error);
	});
}

function findOneRegExp() {
	const filterDoc = {name: new stitch.BSON.BSONRegExp('bask', 'i')};
	itemsCollection.find(filterDoc, {limit: 1}).first().then(result => {
		if (result) {
			console.log("Successfully Found Document: " + JSON.stringify(result));
		} else {
			console.log("No matching document found");
		}
	}, (error) => {
		alert("Error Finding Document: " + error);
	});
}

function findMany() {
	const filterDoc = {"reviews.0": {$exists: true}};
	const optionsDoc = {projection: {_id: 0}, sort: {name: 1}};

	itemsCollection.find(filterDoc, optionsDoc).asArray().then(results => {
		console.log("Successfully found " + results.length + " items");
		for (let i in results) {
			console.log(i + ": " + JSON.stringify(results[i]));
		}
	}, (error) => {
		alert("Error Finding Documents: " + error);
	});
}

function watch() {
	console.log("HI")
}