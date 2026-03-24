import { getRequest, postRequest } from "../api/apiCaller"
import { apiPath } from "../config/apiPath"

export const createSolution = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.createSolution}`,
        params, successCallback, errorCallback
    )
}

export const getSolutionsByProblem = async (
    problemId, params, successCallback, errorCallback) => {
    await getRequest(
        `${apiPath.getSolutionsByProblem}/${problemId}`,
        params,
        (response) => {
            // Lọc dữ liệu để chỉ lấy các trường cần thiết
            const course = response.data
            console.log(course)
            successCallback(course);
        },
        errorCallback
    )
}

export const createComment = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.createComment}`,
        params, successCallback, errorCallback
    )
}

export const getCommentsBySolution = async (
    problemId, params, successCallback, errorCallback) => {
    await getRequest(
        `${apiPath.getCommentsBySolution}/${problemId}/comments`,
        params,
        (response) => {
            // Lọc dữ liệu để chỉ lấy các trường cần thiết
            const course = response.data
            console.log(course)
            successCallback(course);
        },
        errorCallback
    )
}
// export const voteSolution = async (
//     problemId, params, successCallback, errorCallback) => {
//     await getRequest(
//         `${apiPath.voteSolution}/${problemId}`,
//         params,
//         (response) => {
//             // Lọc dữ liệu để chỉ lấy các trường cần thiết
//             const course = response.data
//             console.log(course)
//             successCallback(course);
//         },
//         errorCallback
//     )
// }