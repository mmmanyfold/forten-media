import AWS from 'aws-sdk';
import fileType from 'file-type';

const BUCKET = process.env.BUCKET;

if (!BUCKET) throw new Error('Not BUCKET or regions env var provided');

const S3 = new AWS.S3({
	accessKeyId: process.env.AWS_ID,
	secretAccessKey: process.env.AWS_KEY,
	region: process.env.REGION,
});

export const uploadURL = async (event, context, callback) => {
	let putRequest = null;
	let params = null;

	const {body} = event

	const buffer = new Buffer(body);

	const Type = fileType(buffer);

	if (Type === null) {
		return context.fail("boo!");
	}

	try {
		params = {
			Bucket: BUCKET,
			Key: "foo-file." + Type.ext,
			ContentType: Type.mime,
		};

		putRequest = await getSignedUrlPromise('putObject', params);
		console.log('putRequest::', putRequest);
		return callback(null, {
			statusCode: 200,
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({uploadURL: putRequest}),
		});
	} catch (e) {
		console.log(e);
		return callback(null, error(e.message));
	}


	return {
		statusCode: 200,
		body: JSON.stringify({
			message: `Go Serverless v1.0! ${123}`,
		}),
	};
};

//
// Utilities
//

function error(message) {
	return {"error": {"message": message}};
}

const getSignedUrlPromise = (operation, params) =>
	new Promise((resolve, reject) => {
		S3.getSignedUrl(operation, params, (err, url) => {
			err ? reject(err) : resolve(url);
		});
	});
