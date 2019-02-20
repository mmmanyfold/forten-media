import AWS from 'aws-sdk';
import moment from 'moment';

const BUCKET = process.env.BUCKET;

if (!BUCKET) throw new Error('Not BUCKET or regions env var provided');

const S3 = new AWS.S3({
	accessKeyId: process.env.AWS_ID,
	secretAccessKey: process.env.AWS_KEY,
	region: process.env.REGION,
});

export const uploadUrl = async (event, context, callback) => {
	const MAX_BYTES = 100000000; // 100mg
	let uploadUrlRequest = null;
	let params = null;

	try {
		const { body } = event;
		const { type, size, name } = JSON.parse(body);
		const unix = moment().unix();

		if (type != 'application/pdf') {
			return {
				statusCode: 400,
				headers: {'Content-Type': 'application/json'},
				body: JSON.stringify({
					error: 'Bad request, check file is PDF and not empty'
				}),
			};
		} else if (size >= MAX_BYTES) {
			return {
				statusCode: 400,
				headers: {'Content-Type': 'application/json'},
				body: JSON.stringify({
					error: 'Bad request, check file is less than 100mgs'
				}),
			};
		}

		try {
			params = {
				Bucket: BUCKET,
				Key: `uploads/${unix}/${name}`,
				ContentType: type,
			};

			uploadUrlRequest = await getSignedUrlPromise('putObject', params);
			return {
				statusCode: 200,
				headers: {
					'Content-Type': 'application/json',
					'Access-Control-Allow-Origin': 'http://localhost:3000',
				},
				body: JSON.stringify({
					uploadUrl: uploadUrlRequest,
				}),
			};
		} catch (e) {
			return {
				statusCode: 500,
				headers: {'Content-Type': 'application/json'},
				body: JSON.stringify({
					error: e.message,
				}),
			};
		}
	} catch (e) {
		throw e.message;
		return {
			statusCode: 400,
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				error: 'Bad request, check file is PDF and not empty'
			}),
		};
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
