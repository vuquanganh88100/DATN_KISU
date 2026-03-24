import { useNavigate } from "react-router-dom";
import { getRequest, postRequest } from "../api/apiCaller";
import { apiPath } from "../config/apiPath";
import useNotify from "../hooks/useNotify";
import { appPath } from "../config/appPath";
import axios from "axios";
export const studentCourseList = async (params, successCallback, errorCallback) => {
    await getRequest(
        `${apiPath.getStudentCourseList}`,
        params,
        (response) => {
            // Lọc dữ liệu để chỉ lấy các trường cần thiết
            const studentCourse = response.data.content;
            console.log(studentCourse)
            const totalElements = response.data.totalElements
            successCallback(studentCourse, totalElements);
        },
        errorCallback
    );
};
export const getCourseDetail = async (onlineCourseId, params, successCallback, errorCallback) => {
    const url = `${apiPath.getCourseDetaiWithoutEnroll}/${onlineCourseId}`
    await getRequest(
        url,
        params,
        (response) => {
            // Lọc dữ liệu để chỉ lấy các trường cần thiết
            const onlineCourseDetail = response.data
            console.log(onlineCourseDetail)
            successCallback(onlineCourseDetail);
        },
        errorCallback
    );
};
export const enrollCourse = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.enrollCourse}`,
        params, successCallback, errorCallback
    )
}
export const learningCourse = async (onlineCourseId, params, successCallback, errorCallback) => {
    const url = `${apiPath.learningCourse}/${onlineCourseId}`;

    await getRequest(
        url,
        params,
        (response) => {
            console.log(response);
            const onlineCourseDetail = response.data;
            successCallback(onlineCourseDetail);
        },
        (error) => {
            console.log(error);
            if (error.response && error.response.status === 403) {
                const errorMessage = error.response.data.message;
                console.log("403 Error Message:", errorMessage);
                alert(errorMessage);
            }
            if (errorCallback) {
                errorCallback(error);
            }
        }
    );
};
export const learningLecture = async (onlineCourseId, lectureId, params, successCallback, errorCallback) => {
    const url = `${apiPath.learningCourse}/${onlineCourseId}/lecture/${lectureId}`;
    await getRequest(
        url,
        params,
        (response) => {
            console.log(response);
            const lectureDetail = response.data;
            successCallback(lectureDetail);
        },
        (error) => {
            console.log(error);
            if (error.response && error.response.status === 403) {
                const errorMessage = error.response.data.message;
                console.log("403 Error Message:", errorMessage);
                alert(errorMessage);
            }
            if (errorCallback) {
                errorCallback(error);
            }
        }
    );
};
export const sendAnsLecture = async (
    lectureId,
    isCorrect,
    successCallback,
    errorCallback
) => {
    const url = `${apiPath.saveProgress}/${lectureId}/saveQuestion`;
    const params = { studentAns: isCorrect }; // Đóng gói thành JSON
    console.log("Sending request with params:", params, "to URL:", url);

    await postRequest(
        url,
        params,
        successCallback,
        errorCallback
    );
};
export const sendTempTime = async (
    lectureId,
    time,
    successCallback,
    errorCallback
) => {
    const url = `${apiPath.saveProgress}/${lectureId}/saveTime`;
    const params = { timeProgress: time }; // Đóng gói thành JSON
    console.log("Sending request with params:", params, "to URL:", url);

    const handleSuccess = (response) => {
        const responseData = response.data;
        console.log("Response data:", responseData);
        if (successCallback) {
            successCallback(responseData);
        }
    };

    await postRequest(
        url,
        params,
        handleSuccess,  
        errorCallback
    );
};

export const checkStatus=async(
    lectureId,
    params,
    successCallback,
    errorCallback
)=>{
    const url =`${apiPath.saveProgress}/${lectureId}/checkStatus`
    await getRequest(
        url,
        params,
        (response) => {
            console.log(response);
            const studentLectureProgress = response.data;
            successCallback(studentLectureProgress);
        },
        (error) => {
           console.log(error)
        }
    );
}
export const resetStudentProgress = async (
    lectureId,
    successCallback,
    errorCallback
) => {
    const url = `${apiPath.saveProgress}/${lectureId}/reset`;
    await postRequest(
        url,
        successCallback,
        errorCallback
    );
};
export const getMyAllCourses = async (params, successCallback, errorCallback) => {
    await getRequest(
        `${apiPath.learningCourse}`,
        params,
        (response) => {
            console.log(response)
            // Lọc dữ liệu để chỉ lấy các trường cần thiết
            const myCourses = response.data
            console.log(myCourses)
            successCallback(myCourses);
        },
        errorCallback
    );
};
export const exportPdfLectureQuestion = async (lectureId, params, successCallback, errorCallback) => {
    const url = `${apiPath.exportLectureQuestion}/${lectureId}`;

    try {
        const response = await axios.get(url, {
            params: params,
            responseType: 'blob', 
        });

        const blob = new Blob([response.data], { type: 'application/pdf' });
        const link = document.createElement('a');
        const objectUrl = window.URL.createObjectURL(blob);
        link.href = objectUrl;
        link.download = 'questions.pdf';
        document.body.appendChild(link);
        link.click();
        link.remove();

        window.URL.revokeObjectURL(objectUrl);

        if (successCallback) successCallback(response);
    } catch (error) {
        console.error('Error exporting PDF:', error);
        if (errorCallback) errorCallback(error);
    }
};

