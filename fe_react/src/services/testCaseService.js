import { postRequest } from "../api/apiCaller";
import { apiPath } from "../config/apiPath";

export const generateInput = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.generateInput}`,
        params,
        successCallback,
        errorCallback
    );
};

export const generateTestCase = async (
    params,
    successCallback,
    errorCallback
) => {
    // Gửi JSON array bình thường, không encode base64
    // params là mảng các objects: [{ input, sourceCode, languageId }, ...]
    await postRequest(
        `${apiPath.generateTestCase}`,
        params,
        successCallback,
        errorCallback
    );
};