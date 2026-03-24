import {
	getRequest,
	postRequest,
	putRequest,
	deleteRequest,
} from "../api/apiCaller";
import { apiPath } from "../config/apiPath";
export const getPagingSubjectsService = async (
	search,
	page,
	size,
	sort,
	successCallback,
	errorCallback
) => {
	const params = {
		page,
		size,
		sort,
	};

	if (search) {
		params.search = search;
	}

	const queryString = Object.entries(params)
		.map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
		.join("&");

	const apiUrl = `${apiPath.allSubjects}?${queryString}`;

	await getRequest(apiUrl, null, successCallback, errorCallback);
};

export const getSubjectByCodeService = async (
	code,
	params,
	successCallback,
	errorCallback
) => {
	await getRequest(
		`${apiPath.getSubjectByCode}/${code}`,
		params,
		successCallback,
		errorCallback
	);
};
export const updateSubjectsService = async (
	subjectId,
	params,
	successCallback,
	errorCallback
) => {
	await putRequest(
		`${apiPath.updateSubject}/${subjectId}`,
		params,
		successCallback,
		errorCallback
	);
};
export const addSubjectsService = async (
	params,
	successCallback,
	errorCallback
) => {
	await postRequest(
		`${apiPath.addSubject}`,
		params,
		successCallback,
		errorCallback
	);
};
export const deleteSubjectsService = async (
	subjectId,
	params,
	successCallback,
	errorCallback
) => {
	await deleteRequest(
		`${apiPath.deleteSubject}/${subjectId}`,
		params,
		successCallback,
		errorCallback
	);
};
export const deleteChaptersService = async (
	code,
	params,
	successCallback,
	errorCallback
) => {
	await deleteRequest(
		`${apiPath.disableChapter}/${code}`,
		params,
		successCallback,
		errorCallback
	);
};
export const addChapterService = async (
	param,
	successCallback,
	errorCallback
) => {
	await postRequest(
		`${apiPath.addChapter}`,
		param,
		successCallback,
		errorCallback
	);
};
export const getChaptersService = async (
	code,
	param,
	successCallback,
	errorCallback
) => {
	await getRequest(
		`${apiPath.addChapters}/${code}/chapter/list`,
		param,
		successCallback,
		errorCallback
	);
};
