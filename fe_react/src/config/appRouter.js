import AutomaticScoring from "../pages/AutomaticScoring/AutomaticScoring";
import AdminDashboard from "../pages/Dashboard/AdminDashboard/AdminDashboard";
import StudentDashboard from "../pages/Dashboard/StudentDashboard/StudentDashboard";
import TeacherDashboard from "../pages/Dashboard/TeacherDashboard/TeacherDashboard";
import ExamClassAdd from "../pages/ExamClass/ExamClassCreate/ExamClassCreate";
import ExamClassDetail from "../pages/ExamClass/ExamClassDetail/ExamClassDetail";
import ExamClassEdit from "../pages/ExamClass/ExamClassEdit/ExamClassEdit";

import ExamClassList from "../pages/ExamClass/ExamClassList/ExamClassList";
import Home from "../pages/Home/Home";
import Library from "../pages/Library/Library";
import Login from "../pages/Login/Login";
import NotFound from "../pages/NotFound";
import ProfileUser from "../pages/ProfileUser/ProfileUser";
import AddQuestions from "../pages/Questions/AddQuestion/AddQuestions";
import QuestionEdit from "../pages/Questions/QuestionEdit/QuestionEdit";
import QuestionList from "../pages/Questions/QuestionList/QuestionList";
import StudentEdit from "../pages/Students/StudentEdit/StudentEdit";
import StudentList from "../pages/Students/StudentList/StudentList";
import StudentView from "../pages/Students/StudentView/StudentView";
import SubjectAdd from "../pages/Subjects/SubjectAdd/SubjectAdd";
import SubjectEdit from "../pages/Subjects/SubjectEdit/SubjectEdit";
import SubjectList from "../pages/Subjects/SubjectList/SubjectList";
import SubjectView from "../pages/Subjects/SubjectView/SubjectView";
import TeacherEdit from "../pages/Teachers/TeacherEdit/TeacherEdit";
import TeacherList from "../pages/Teachers/TeacherList/TeacherList";
import TeacherView from "../pages/Teachers/TeacherView/TeacherView";
import TestCreate from "../pages/Test/TestCreate/TestCreate";
import TestEdit from "../pages/Test/TestEdit/TestEditFill/TestEdit";
import TestList from "../pages/Test/TestList/TestList";
import TestSetCreate from "../pages/TestSet/TestSetCreate/TestSetCreate";
import TimeTable from "../pages/TimeTable/TimeTable";
import CreateUser from "../pages/User/CreateUser/CreateUser";
import DepartmentList from "../pages/Department/DepartmentList";
import { appPath } from "./appPath";
import StudentTestList from "../pages/StudentTest/StudentTestList/StudentTestList";
import StudentTestDetails from "../pages/StudentTest/StudentTestDetails/StudentTestDetails";
import { isAuthenticated } from "../utils/roleUtils"
import SystemConfiguration from "../pages/SystemConfiguration/SystemConfiguration";
import AdminList from "../pages/User/Admin/AdminList";
import AdminEdit from "../pages/User/Admin/AdminEdit/AdminEdit";
import OnlineCourseList from "../pages/OnlineCourse/OnlineCoursesList/OnlineCourseList";
import OnlineCourseAdd from "../pages/OnlineCourse/OnlineCourseAdd/OnlineCourseAdd";
import LectureAdd from "../pages/Lecture/LectureAdd/LectureAdd";
import StudentOnlineCourseList from "../pages/StudentOnlineCourse/StudentOnlineCourseList/StudentOnlineCourseList";
import StudentOnlineCourseDetail from "../pages/StudentOnlineCourse/Learning/NotEnroll/StudentOnlineCourseDetail";
import StudentLearning from "../pages/StudentOnlineCourse/Learning/Enroll/StudentLearning";
import CourseDashBoard from "../pages/StudentOnlineCourse/CourseDashBoard/CourseDashBoard";
import { compose } from "redux";
import LectureEdit from "../pages/Lecture/LecutureEdit/LectureEdit";
import TestCourseAdd from "../pages/TestCourse/TestCourseAdd";
import OnlineCourseStatistic from "../pages/OnlineCourse/OnlineCourseStatistic/OnlineCourseStatistic";
import TopicContest from "../pages/TopicContest/TopicContest";
import TopicContestDetail from "../pages/TopicContest/TopicContestDetail";
import StatiscticProblemContest from "../pages/TopicContest/StatisticProblemContest/StatiscticProblemContest";
import ProblemContest from "../pages/ProblemContest/ProblemContest";
import StudentContestList from "../pages/StudentContest/StudentContestList/StudentContestList";
import StudentContestDetailRefactored from "../pages/StudentContest/StudentContestDetail/StudentContestDetailRefactored";
import MyProblemContest from "../pages/StudentContest/MyProblemContest";

const publicRoutes = [
    { path: appPath.notFound, component: NotFound },
    { path: appPath.default, component: (isAuthenticated() ? ExamClassList : StudentList) },
    { path: appPath.home, component: Home },
    { path: appPath.studentDashboard, component: StudentDashboard },
    { path: appPath.teacherDashboard, component: TeacherDashboard },
    { path: appPath.adminDashboard, component: AdminDashboard },
    //student
    { path: appPath.studentAdd, component: TestEdit },
    { path: appPath.studentEdit, component: StudentEdit },
    { path: appPath.studentEdit + "/:code", component: StudentEdit },
    { path: appPath.studentList, component: StudentList },
    { path: appPath.studentView, component: StudentView },
    //teacher
    { path: appPath.teacherEdit, component: TeacherEdit },
    { path: appPath.teacherEdit + "/:code", component: TeacherEdit },
    { path: appPath.teacherList, component: TeacherList },
    { path: appPath.teacherView, component: TeacherView },
    //subject
    { path: appPath.subjectEdit, component: SubjectEdit },
    { path: appPath.subjectAdd, component: SubjectAdd },
    { path: appPath.subjectList, component: SubjectList },
    { path: appPath.subjectView, component: SubjectView },
    { path: appPath.subjectView + "/:code", component: SubjectView },
    { path: appPath.subjectEdit + "/:code", component: SubjectEdit },
    //exam: ExamList },
    { path: appPath.automaticScoring, component: AutomaticScoring },
    { path: appPath.library, component: Library },
    { path: appPath.timeTable, component: TimeTable },
    //question
    { path: appPath.listQuestions, component: QuestionList },
    { path: appPath.questionEdit, component: QuestionEdit },
    { path: appPath.questionEdit + "/:id", component: QuestionEdit },
    { path: appPath.addQuestions, component: AddQuestions },
    //test
    { path: appPath.testList, component: TestList },
    { path: appPath.testCreate, component: TestCreate },
    { path: appPath.testSetCreate, component: TestSetCreate },
    { path: appPath.testSetCreate + "/:testId", component: TestSetCreate },
    { path: `${appPath.testEdit}/:testSetId/:testId`, component: TestEdit },
    {
        path: appPath.login,
        component: Login,
        layout: "SignLayout",
        // isPrivateRouter: true,
    },
    // exam class
    { path: appPath.examClassList, component: ExamClassList },
    { path: appPath.examClassCreate, component: ExamClassAdd },
    { path: appPath.examClassEdit, component: ExamClassEdit },
    { path: appPath.examClassEdit + "/:id", component: ExamClassEdit },
    { path: appPath.examClassDetail + "/:id", component: ExamClassDetail },
    // user
    { path: appPath.createUser + "/:userType", component: CreateUser },
    { path: appPath.profileUser, component: ProfileUser },
    { path: appPath.adminList, component: AdminList },
    { path: appPath.adminEdit, component: AdminEdit },
    { path: appPath.adminEdit + "/:id", component: AdminEdit },

    // student test
    { path: appPath.studentTestList, component: StudentTestList },
    { path: appPath.onlineStudentTestDetails + "/:studentTestSetId", component: StudentTestDetails },

    // department
    { path: appPath.departmentList, component: DepartmentList },
    //online course
    { path: appPath.onlineCourseAdd, component: OnlineCourseAdd },
    { path: appPath.onlineCourseList, component: OnlineCourseList },
    { path: appPath.onlineCourseEdit + "/:id", component: OnlineCourseAdd },
    { path: appPath.OnlineCourseStatistic + "/:id", component: OnlineCourseStatistic },
    //lecture
    { path: "/online-course/:courseId" + appPath.lectureAdd, component: LectureAdd },
    { path: "/online-course/:courseId" + appPath.lectureAdd + "/:lectureId", component: LectureEdit },
    //test course
    { path: "/online-course/:courseId" + appPath.testCourseAdd, component: TestCourseAdd },

    // student-online-course
    { path: appPath.studentCourseList, component: StudentOnlineCourseList },
    { path: appPath.CourseWithoutEnroll + "/:courseId/detail", component: StudentOnlineCourseDetail },
    { path: appPath.studentLearning + "/:courseId", component: StudentLearning },
    { path: appPath.myCourses, component: CourseDashBoard },

    //topic-contest
    { path: appPath.topicContest, component: TopicContest },
    { path: appPath.topicContestView + "/:id", component: TopicContestDetail },
	{ path: appPath.topicContestProblemStatistic + "/:problemId", component: StatiscticProblemContest },

    //problem-contest
    { path: appPath.problemContest, component: ProblemContest },

    //student-problem-contest
    { path: appPath.studentProblemContestList, component: StudentContestList },
    { path: "/student-problem-contest/detail/:id", component: StudentContestDetailRefactored },
    { path: appPath.myProblemContest, component: MyProblemContest },

    // system configuration
    { path: appPath.systemConfiguration, component: SystemConfiguration }
    // private routes
];

const privateRoutes = [];
export { privateRoutes, publicRoutes };
