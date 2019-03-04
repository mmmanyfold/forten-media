import AWS from 'aws-sdk';
import moment from 'moment';

const BUCKET = process.env.BUCKET;

if (!BUCKET) throw new Error('Not BUCKET or regions env var provided');

const S3 = new AWS.S3({
	accessKeyId: process.env.AWS_ID,
	secretAccessKey: process.env.AWS_KEY,
	region: process.env.REGION,
});

export const s3Url = async (event) => {
	try {
		const json = JSON.parse(event.body);
		const {  operation } = json;
		switch (operation) {
			case 'putObject' :
				return putObject(json);
			case 'getObject' :
				return getObject(json);
			default:
				return {
					statusCode: 400,
					headers: {'Content-Type': 'application/json'},
					body: JSON.stringify({
						error: 'Bad request, operation not implemented'
					}),
				};
		}
	} catch (err) {
		return {
			statusCode: 400,
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				error: err.message,
			}),
		}
	}
};

async function putObject(json) {
	const MAX_BYTES = 100000000; // 100mg
	const { type, size, name } = json;
	const unix = moment().unix();
	const params = {
		Bucket: BUCKET,
		Key: `uploads/${unix}/${name}`,
		ContentType: type,
	};
	try {
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
			return {
				statusCode: 200,
				headers: {
					'Content-Type': 'application/json',
					'Access-Control-Allow-Origin': '*',
				},
				body: JSON.stringify({
					uploadUrl: await getSignedUrlPromise('putObject', params),
					key: `uploads/${unix}/${name}`,
				}),
			};
		} catch (err) {
			return {
				statusCode: 500,
				headers: {'Content-Type': 'application/json'},
				body: JSON.stringify({
					error: err.message,
				}),
			};
		}
	} catch (_) {
		return {
			statusCode: 400,
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				error: 'Bad request, check file is PDF and not empty'
			}),
		};
	}
}

async function getObject(json) {
	const { key } = json;
	const params = {
		Bucket: BUCKET,
		Key: key,
	};
	try {
		return {
			statusCode: 200,
			headers: {
				'Content-Type': 'application/json',
				'Access-Control-Allow-Origin': '*',
			},
			body: JSON.stringify({
				url: await getSignedUrlPromise('getObject', params),
			}),
		};
	} catch (err) {
		return {
			statusCode: 500,
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				error: err.message,
			}),
		};
	}
}


//
// Utilities
//

const getSignedUrlPromise = (operation, params) =>
	new Promise((resolve, reject) => {
		S3.getSignedUrl(operation, params, (err, url) => {
			err ? reject(err) : resolve(url);
		});
	});
