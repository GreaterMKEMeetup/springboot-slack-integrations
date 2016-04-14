var response = {};

try {
	var inputObj = JSON.parse(jsonString);
} catch (err) {
	response.message = err.message;
	//noinspection JSAnnotator,JSAnnotator
	return response;
}