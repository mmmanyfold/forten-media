import AWS from 'aws-sdk';
import fileType from 'file-type';
import moment from 'moment';

const BUCKET = process.env.BUCKET;

if (!BUCKET) throw new Error('Not BUCKET or regions env var provided');

const S3 = new AWS.S3({
	accessKeyId: process.env.AWS_ID,
	secretAccessKey: process.env.AWS_KEY,
	region: process.env.REGION,
});

export const uploadUrl = async (event, context, callback) => {
	let uploadUrlRequest = null;
	let params = null;
	const { body } = event
	const buffer = new Buffer(body);
	const Type = fileType(buffer);
	const unix = moment().unix();

	if (Type === null && Type.ext != 'pdf') {
		return callback(null, {
			statusCode: 400,
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				error: 'Bad request, check file is PDF and not empty'
			}),
		});
	}

	try {
		params = {
			Bucket: BUCKET,
			Key: `${unix}.${Type.ext}`,
			ContentType: Type.mime,
			ACL: 'public-read',
		};

		uploadUrlRequest = await getSignedUrlPromise('putObject', params);

		return callback(null, {
			statusCode: 200,
			headers: {
				'Content-Type': 'application/json',
				'Access-Control-Allow-Origin': 'http://localhost:3000',
			},
			body: JSON.stringify({
				uploadUrl: uploadUrlRequest,
			}),
		});
	} catch (e) {
		return callback(null, {
			statusCode: 500,
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				error: e.message,
			}),
		});
	}
};

//
// Utilities
//

const getSignedUrlPromise = (operation, params) =>
	new Promise((resolve, reject) => {
		S3.getSignedUrl(operation, params, (err, url) => {
			err ? reject(err) : resolve(url);
		});
	});
