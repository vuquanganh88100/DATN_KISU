import { getRequest, postRequest } from "../api/apiCaller";
import { apiPath } from "../config/apiPath";

export const createProblemContest = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.createProblemContest}`,
        params,
        successCallback,
        errorCallback
    );
};
export const getStudentProblemContests = async (
    params,
    successCallback,
    errorCallback
) => {
    await getRequest(
        `${apiPath.studentProblemContests}`,
        params,
        successCallback,
        errorCallback
    );
};
export const getContestDetail = async (problemContestId, params, successCallback, errorCallback) => {
    const url = `${apiPath.studentProblemContests}/${problemContestId}`
    await getRequest(
        url,
        params,
        (response) => {
            const problemContestDetail = response.data
            console.error(response)
            successCallback(problemContestDetail);
        },
        errorCallback
    );
};
export const getSubmissionsByProblem = async (problemContestId, params, successCallback, errorCallback) => {
    const url = `${apiPath.getSubmissionsByProblem}/${problemContestId}`
    await getRequest(
        url,
        params,
        (response) => {
            const problemContestDetail = response.data
            console.error(response)
            successCallback(problemContestDetail);
        },
        errorCallback
    );
};