
function updateOneAddField() {
	const filterDoc = {name: "legos"};
	const updateDoc = {$set: {
		name: "blocks", 
		price: 20.99, 
		category: "toys"
	}}

	itemsCollection.updateOne(filterDoc, updateDoc).then(result => {
		console.log("Successfully matched " + result.matchedCount + " and " + 
					"modified " + result.modifiedCount + " items.")
	}, (error) => {
	    alert("Error Updating Document: " + error);
	});
}

function updateOnePush() {
	const filterDoc = {name: "football"};
	const updateDoc = {$push: {reviews: {
		username: "stitchfan2018", 
		comment: "what a neat product"
	}}};

	itemsCollection.updateOne(filterDoc, updateDoc).then(result => {
		console.log("Successfully matched " + result.matchedCount + " and " + 
					"modified " + result.modifiedCount + " items.")
	}, (error) => {
	    alert("Error Updating Document: " + error);
	});
}

function updateOneUpsert() {
	const filterDoc = {name: "board games"};
	const updateDoc = {$inc: {quantity: 5}};

	itemsCollection.updateOne(filterDoc, updateDoc, {upsert: true}).then(result => {
		if (result.upsertedId) {
			console.log("Successfully upserted item with id: " + result.upsertedId);
		} else {
			console.log("Successfully matched " + result.matchedCount + " and " + 
						"modified " + result.modifiedCount + " items.")
		}
	}, (error) => {
	    alert("Error Updating Document: " + error);
	});
}

function updateMany() {
	const filterDoc = {};
	const updateDoc = {$mul: {quantity: 10}};

	itemsCollection.updateMany	(filterDoc, updateDoc).then(result => {
		console.log("Successfully matched " + result.matchedCount + " and " + 
					"modified " + result.modifiedCount + " items.")
	}, (error) => {
	    alert("Error Updating Documents: " + error);
	});

}