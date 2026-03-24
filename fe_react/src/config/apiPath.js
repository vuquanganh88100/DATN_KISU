import { getCommentsBySolution } from "../services/solutionServices";
import { getTestCourseDetail } from "../services/testCourseService";
import { getProblemContestByTopic } from "../services/topicContestService";

export const API_PATH = "/e-learning/api"; // for using reverse proxy by nginx

export const DEFAULT_PATH = "/e-learning";

// check BASE_URL in different envs
export const BASE_URL = API_PATH;

export const BASE_RESOURCE_URL = process.env.REACT_APP_BE_RESOURCES_HOST + DEFAULT_PATH;

export const apiPath = {
    // Authentication
    login: BASE_URL + "/auth/login",
    profile: BASE_URL + "/user/profile",
    updateProfile: BASE_URL + "/user/profile",
    refreshToken: BASE_URL + "/auth/token/refresh",
    // Student
    allStudents: BASE_URL + "/user/student/list",
    pageStudent: BASE_URL + "/user/student/page",
    updateStudent: BASE_URL + "/student/update",
    addStudent: BASE_URL + "/student/add",
    deleteStudent: BASE_URL + "/student/disable",
    exportStudents: BASE_URL + "/student/export",
    // AI Scoring
    automaticScoring: BASE_URL + "/test-set/scoring",
    resetTableResult: BASE_URL + "/test-set/scoring/result",
    saveTableResult: BASE_URL + "/test-set/scoring/result",
    imgInFolder: BASE_URL + "/test-set/handled-answers/uploaded",
    deleteImgInFolder: BASE_URL + "/test-set/handled-answers/delete",
    loadLatestTempScoredData: BASE_URL + "/test-set/scored-data/exam-class",


    //Teacher:
    allTeachers: BASE_URL + "/user/teacher/list",
    pageTeacher: BASE_URL + "/user/teacher/page",
    updateTeacher: BASE_URL + "/teacher/update",
    addTeacher: BASE_URL + "/teacher/add",
    deleteTeacher: BASE_URL + "/teacher/disable",
    exportStudent: BASE_URL + "/teacher/export",
    //Subject:
    allSubjects: BASE_URL + "/subject/list",
    getSubjectByCode: BASE_URL + "/subject/detail",
    updateSubject: BASE_URL + "/subject",
    addSubject: BASE_URL + "/subject",
    deleteSubject: BASE_URL + "/subject/disable",
    //Chapter:
    disableChapter: BASE_URL + "/chapter/disable",
    updateChapter: BASE_URL + "/chapter",
    addChapter: BASE_URL + "/chapter",
    // TODO: CHECK URL
    addChapters: BASE_URL + "/v1",
    //Question:
    addQuestion: BASE_URL + "/question",
    getQuestionbyCode: BASE_URL + "/question",
    deleteQuestion: BASE_URL + "/question",
    updateQuestion: BASE_URL + "/question",
    getQuestionDetail: BASE_URL + "/question/detail",
    importQuestion: BASE_URL + "/question/import",
    //Test:
    testRandomCreate: BASE_URL + "/test/create/random",
    testCreate: BASE_URL + "/test/create",
    allTest: BASE_URL + "/test",
    updateTest: BASE_URL + "/test",
    disableTest: BASE_URL + "/test/disable",
    deleteTest: BASE_URL + "/test",
    detailsTest: BASE_URL + "/test/details",
    listAllowedQuestions: BASE_URL + "/test/questions/allowed",
    //Test-set:
    testSetCreate: BASE_URL + "/test-set/generate",
    testSetDetail: BASE_URL + "/test-set/detail",
    updateTestSet: BASE_URL + "/test-set",
    testSetManual: BASE_URL + "/test-set/create",
    testSetList: BASE_URL + "/test-set/list",
    deleteTestSet: BASE_URL + "/test-set",
    //Exam-class:
    examClassCreate: BASE_URL + "/exam-class",
    pageExamClasses: BASE_URL + "/exam-class/page",
    examClassDetail: BASE_URL + "/exam-class/detail",
    disableExamClass: BASE_URL + "/exam-class/disable",
    updateExamClass: BASE_URL + "/exam-class",
    getParticipant: BASE_URL + "/exam-class/participant/list",
    getExamClassResult: BASE_URL + "/std-test-set/result",
    importStudent: BASE_URL + "/exam-class/participant/student/import",
    assignTestSet: BASE_URL + "/exam-class/test-set/assign",
    publishTestSet: BASE_URL + "/exam-class/test-set/publish",
    sendEmailExamClassResult: BASE_URL + "/exam-class/result/send-email",
    //combo box
    comboSubject: BASE_URL + "/combobox/subject",
    comboViewableSubject: BASE_URL + "/combobox/subject/viewable",
    comboChapter: BASE_URL + "/combobox/subject/chapter",
    comboSemester: BASE_URL + "/combobox/semester",
    comboStudent: BASE_URL + "/combobox/user/student",
    comboTeacher: BASE_URL + "/combobox/user/teacher",
    comboTest: BASE_URL + "/combobox/test",
    comboExamClass: BASE_URL + "/combobox/exam-class",
    comboDepartment: BASE_URL + "/combobox/department",
    comboChapterByCourse: BASE_URL + "/combobox/online/course/",
    // user
    createUser: BASE_URL + "/user",
    updateUser: BASE_URL + "/user",
    infoUser: BASE_URL + "/user",
    deleteUser: BASE_URL + "/user/hard",
    pageAdmin: BASE_URL + "/user/admin/page",
    updatePassword: BASE_URL + "/user/password/update",

    // static files
    questionImportTemplate: BASE_RESOURCE_URL + "/resources/excelTemplate/Questions.xlsx",
    studentImportTemplate: BASE_RESOURCE_URL + "/resources/excelTemplate/Dump_UserStudent.xlsx",
    teacherImportTemplate: BASE_RESOURCE_URL + "/resources/excelTemplate/Dump_UserTeacher.xlsx",
    answerSheetTemplate: BASE_RESOURCE_URL + "/resources/wordTemplate/AnswerSheetTemplate.pdf", // wait for get template

    // Student Test (online)
    studentOpeningTestList: BASE_URL + "/std-test-set/online-test",
    studentClosedTestList: BASE_URL + "/std-test-set/closed-test",
    studentTestAttempt: BASE_URL + "/std-test-set/attempt",
    studentTestSubmit: BASE_URL + "/std-test-set/submit",
    studentTestSavedTempSubmission: BASE_URL + "/std-test-set/temp-submission",
    studentTestDetails: BASE_URL + "/std-test-set/details",
    loadStudentTestSet: BASE_URL + "/std-test-set/details/test-set",

    //Course Online
    addNewCourse: BASE_URL + "/online/course/add",
    listCourse: BASE_URL + "/online/course/page",
    getCourseById: BASE_URL + "/online/course",
    updatePublish: BASE_URL + "/online/course",
    getDetailCourse: BASE_URL + "/online/course/detail",
    statisticCourse: BASE_URL + "/online/course/statistic",

    // lecture
    uploadLecture: BASE_URL + "/online/course/lecture",
    // getListLecture:BASE_URL+"/online/course",
    getLectureDetail: BASE_URL + "/online/course/lecture",
    //test-course
    uploadTestCourse: BASE_URL + "/online/course/create-test",
    getTestCourseOverview: BASE_URL + "/online/course/test",
    getTestCourseDetail: BASE_URL + "/online/course/detail-test",
    submitTestCourse: BASE_URL + "/online/course/student-test/save/ans",
    getTestCourseResult: BASE_URL + "/online/course/student-test/result",
    downloadTestCourse: BASE_URL + "/online/course/export/test-course",
    //upload lecture material
    uploadFileAttachment: BASE_URL + "/online/course/upload-fileAttachment",
    uploadVideo: BASE_URL + "/online/course/upload-video",
    //Upload
    uploadImage: BASE_URL + "/file-attach/upload-img",
    uploadDoc: BASE_URL + "/file-attach/upload-doc",

    // student  course 
    getStudentCourseList: BASE_URL + "/online/student-course/page",
    getCourseDetaiWithoutEnroll: BASE_URL + "/online/student-course",
    enrollCourse: BASE_URL + "/online/student-course/enroll",
    learningCourse: BASE_URL + "/online/student-course/learning",
    saveProgress: BASE_URL + "/online/student-course/learning",
    exportLectureQuestion: BASE_URL + "/online/student-course/export-questions",
    getLectureProgress: BASE_URL + "/online/course/lecture/progress",

    // Notifications
    getNotifications: BASE_URL + "/notifications",
    registerFCMToken: BASE_URL + "/notifications/fcm/register-token",
    updateNewNotification: BASE_URL + "/notifications/new-status/update",
    countNewNotifications: BASE_URL + "/notifications/new-status/count",

    //topic-contest
    addTopicContest: BASE_URL + "/topic-contest",
    getProblemContestByTopic   : BASE_URL + "/problem-contest/topic",
    // problem-contest
    createProblemContest: BASE_URL + "/problem-contest",
    studentProblemContests: BASE_URL + "/problem-contest",
    submitCode: BASE_URL + "/problem-contest/submit-code",
    runCode: BASE_URL + "/problem-contest/run-code",


    //test-case
    generateInput: BASE_URL + "/test-case/generate-input",
    generateTestCase: BASE_URL + "/test-case/generate-testcase",

    // solution and comment
    createSolution : BASE_URL + "/problem/solution",
    getSolutionsByProblem:  BASE_URL + "/problem/solution/problem",
    createComment: BASE_URL + "/problem/solution/comment",
    voteSolution: BASE_URL + "/problem/solution/vote",
    getCommentsBySolution: BASE_URL + "/problem/solution",

    // chat assistant
    chatBot: BASE_URL + "/chat",
    
    //submission
    getSubmissionsByStudent: BASE_URL + "/submissions",
    getSubmissionsByProblem: BASE_URL + "/submissions/problem",
};

