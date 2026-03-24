import React, { act, useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { checkStatus, exportPdfLectureQuestion, learningCourse, learningLecture, resetStudentProgress, sendAnsLecture, sendTempTime } from '../../../../services/studentOnlineCourseService';
import useNotify from '../../../../hooks/useNotify';
import { Button, Col, Collapse, Row } from 'antd';
import ReactPlayer from 'react-player';
import { data } from 'autoprefixer';
import "./StudentLearning.scss"
import { error } from 'ajv/dist/vocabularies/applicator/dependencies';
import TestCourse from './TestCourse/TestCourse';
const { Panel } = Collapse;
function StudentLearning() {
    const location = useLocation();
    const { courseId } = location.state || {};
    const navigate = useNavigate();
    const notify = useNotify();

    // Main states
    const [chapterCourseDetail, setChapterCourseDetail] = useState([]);
    const [currentPosition, setCurrentPosition] = useState(null);
    const [lectureCompleted, setLectureCompleted] = useState([]);
    const [completedTestCourse, setCompletedTestCourse] = useState([]);

    // Video states
    const [videoUrl, setVideoUrl] = useState("");
    const [currentTime, setCurrentTime] = useState(0);
    const [isPlaying, setIsPlaying] = useState(true);
    const [maxWatchedTime, setMaxWatchedTime] = useState(0);

    // Lecture/Test content states
    const [lectureTitle, setLectureTitle] = useState("");
    const [lectureMaterial, setLectureMaterial] = useState([]);
    const [questionList, setQuestionList] = useState([]);
    const [shownQuestions, setShownQuestions] = useState([]);
    const [isCompleted, setIsCompleted] = useState(false);

    // Popup states
    const [showPopup, setShowPopup] = useState(null);
    const [popupReset, showPopupReset] = useState(false);
    const [popupWatchContiue, setPopupWatchContinue] = useState(false);
    const [selectedAns, setSelectedAns] = useState([]);
    const [maxPosition, setMaxPosition] = useState()

    const playerRef = useRef(null);

    // Initial course data fetch
    useEffect(() => {
        const fetchStudentCourseLearning = () => {
            learningCourse(
                courseId,
                {},
                (data) => {
                    console.log(data);
                    setChapterCourseDetail(data.chapterCourseDetailDtos);
                    setLectureCompleted(data.lectureCompleted);
                    setCompletedTestCourse(data.testCourseCompleted || []);

                    if (data.currentPosition) {
                        setCurrentPosition(data.currentPosition);
                        setMaxPosition(data.currentPosition)
                        if (data.currentPosition.type === 'LECTURE') {
                            loadLectureContent(data.currentPosition.id);
                        } else if (data.currentPosition.type === 'TEST_COURSE') {
                            loadTestContent(data.currentPosition.id);
                        }
                    }
                },
                (error) => {
                    notify.error("Bạn cần phải đăng ký khóa học");
                }
            );
        };

        if (courseId) {
            fetchStudentCourseLearning();
        }
    }, [courseId]);
    useEffect(() => {

    }, [currentPosition])
    const loadLectureContent = (lectureId) => {
        setLectureMaterial([]);
        setShowPopup(null);
        setShownQuestions([]);
        setCurrentTime(0);
        setIsPlaying(false);
        // console.log(lectureId);

        learningLecture(
            courseId,
            lectureId,
            {},
            (data) => {
                // console.log("Data received:", data);
                setLectureTitle(data.lectureDto.lectureName);
                setVideoUrl(data.lectureDto.urlVideo);

                const questions = data.lectureDto.lectureQuestionDtos;
                setQuestionList(questions.sort((a, b) => a.timeStart - b.timeStart));
                setIsCompleted(data.lectureStudentProgressDto ? data.lectureStudentProgressDto.completed : false);

                const filteredMaterials = data.lectureDto.lectureMaterialDtos.filter(
                    material => material.storedType === 1
                );
                setLectureMaterial(filteredMaterials);

                // Kiểm tra và xử lý lectureStudentProgressDto
                if (data.lectureStudentProgressDto) {
                    if (!data.lectureStudentProgressDto.completed && data.lectureStudentProgressDto.maxWatchedTime > 0) {
                        setMaxWatchedTime(data.lectureStudentProgressDto.maxWatchedTime + 1);
                        setIsPlaying(false);
                        setPopupWatchContinue(true);
                    } else {
                        setIsPlaying(true);
                    }
                } else {
                    // Nếu data.lectureStudentProgressDto là null hoặc không tồn tại, set IsPlaying = true
                    setIsPlaying(true);
                }
            }
        );
    };
    useEffect(() => {
        if (currentPosition && currentPosition.type === 'LECTURE') {
            loadLectureContent(currentPosition.id);  // Gọi loadLectureContent khi currentPosition thay đổi
        }
    }, [currentPosition]);

    const loadTestContent = (testId) => {
        // Implement test loading logic here
    };

    useEffect(() => {
        const activeQuestion = questionList.find(q => {
            const isLectureCompleted = lectureCompleted.includes(currentPosition?.id);
            return !isLectureCompleted &&
                Math.abs(currentTime - q.timeEnd) < 1 &&
                !shownQuestions.includes(q.id);
        });

        if (activeQuestion) {
            setShownQuestions(prev => [...prev, activeQuestion.id]);
            setShowPopup(activeQuestion);
            setIsPlaying(false);
        }
    }, [currentTime, questionList, shownQuestions]);



    // handle submit ans
    const handleAnswerChange = (answerId, checked) => {
        if (checked) {
            setSelectedAns((prev) => [...prev, answerId]);
        } else {
            setSelectedAns((prev) => prev.filter((id) => id !== answerId));
        }
    };
    const handleClosePopup = async () => {
        const correctAnswers = showPopup.answerResDTOList
            .filter(answer => answer.isCorrect)
            .map(answer => answer.id);

        const isCorrect = selectedAns.every(id => correctAnswers.includes(id)) &&
            correctAnswers.every(id => selectedAns.includes(id));

        try {
            await sendAnsLecture(currentPosition.id, isCorrect);
            await saveTime(1);
            notify.success(isCorrect ? "Bạn đã trả lời đúng!" : "Bạn đã trả lời sai!");
            setSelectedAns([]);
            setShowPopup(null);
            setIsPlaying(true);
        } catch (error) {
            notify.error("Có lỗi xảy ra khi xử lý câu trả lời");
        }
    };

    useEffect(() => {
        console.log("Current showPopup:", showPopup);
    }, [showPopup])
    // console.log(currentTime)
    // handle submit time
    const saveTime = (timeOffset = 0, successCallback) => {
        const timeToSave = Math.round(currentTime + timeOffset);
        sendTempTime(
            currentPosition.id,
            timeToSave,
            (responseData) => {
                if (successCallback) {
                    successCallback(responseData);
                }
            },
            (error) => {
                notify.error("Lỗi khi lưu tiến trình");
            }
        );
    };

    const handleVideoEnded = () => {
        saveTime(0, (responseData) => {

            console.log("Trạng thái sau khi gửi thời gian:", responseData);
            if (!responseData.completed) {
                showPopupReset(true)
            }
        });
    };

    const handlePopupReset = () => {
        resetProgress()
        showPopupReset(false)
    }
    const resetProgress = () => {
        resetStudentProgress(
            currentPosition.id,
            () => {
                notify.success("Đã reset tiến trình");
            },
            (error) => {
                notify.success("Reset tiến trình thành công");
            }
        );
    };

    const handleContinueWatching = () => {
        setPopupWatchContinue(false);
        setIsPlaying(true);

        // Nếu có maxWatchedTime, seekTo đoạn đó
        if (playerRef.current && maxWatchedTime > 0) {
            playerRef.current.seekTo(maxWatchedTime, 'seconds');
        }
    };

    // Hàm xử lý khi chọn xem lại từ đầu
    const handleRestartLecture = () => {
        resetProgress();
        setPopupWatchContinue(false);
        setIsPlaying(true);

        // Seek về đầu video
        if (playerRef.current) {
            playerRef.current.seekTo(0, 'seconds');
        }
    };


    console.log(currentPosition)
    return (
        <div className="student-learning-container">
            <Row gutter={24}>
                <Col xs={24} md={16} lg={18} xl={19}>
                    <div className="learning-container">
                        {currentPosition?.type === 'LECTURE' && (
                            <>
                                <ReactPlayer
                                    ref={playerRef}
                                    url={videoUrl}
                                    width="100%"
                                    height="360"
                                    controls={lectureCompleted.includes(currentPosition.id)}
                                    playing={isPlaying}
                                    onProgress={({ playedSeconds }) => setCurrentTime(playedSeconds)}
                                    onEnded={handleVideoEnded}
                                    style={{
                                        pointerEvents: lectureCompleted.includes(currentPosition.id) ? 'auto' : 'none',
                                        userSelect: lectureCompleted.includes(currentPosition.id) ? 'auto' : 'none',
                                        WebkitUserSelect: lectureCompleted.includes(currentPosition.id) ? 'auto' : 'none',
                                        maxHeight: '70vh'
                                    }}
                                    config={{
                                        file: {
                                            attributes: {
                                                style: !lectureCompleted.includes(currentPosition.id)
                                                    ? {
                                                        pointerEvents: 'none',
                                                        WebkitUserSelect: 'none',
                                                        userSelect: 'none'
                                                    }
                                                    : {}
                                            }
                                        }
                                    }}
                                />
                                {popupWatchContiue && (
                                    <div className="popup-watch-continue">
                                        <div className="popup-watch-continue-content">
                                            <h3>Bạn đã dừng ở giây thứ {maxWatchedTime}</h3>
                                            <p>Bạn muốn tiếp tục xem từ lần cuối cùng hay xem lại từ đầu?</p>
                                            <div className="popup-actions">
                                                <button onClick={handleContinueWatching}>
                                                    Xem tiếp từ lần trước
                                                </button>
                                                <button onClick={handleRestartLecture}>
                                                    Xem lại từ đầu
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                )}
                                {showPopup && (
                                    <div className="popup-overlay">
                                        <div className="popup-content">
                                            <h3>Câu hỏi:</h3>
                                            <div dangerouslySetInnerHTML={{ __html: showPopup.content }} />
                                            <ul>
                                                {showPopup.answerResDTOList.map((answer) => (
                                                    <li key={answer.id}>
                                                        <label>
                                                            <input
                                                                type={showPopup.multipleAns ? "checkbox" : "radio"}
                                                                name="answer"
                                                                onChange={(e) => handleAnswerChange(answer.id, e.target.checked)}
                                                            />
                                                            <span dangerouslySetInnerHTML={{ __html: answer.content }} />
                                                        </label>
                                                    </li>
                                                ))}
                                            </ul>
                                            <button onClick={handleClosePopup}
                                            >Submit</button>
                                        </div>
                                    </div>
                                )}
                                {popupReset && (
                                    <div className="popup-reset">
                                        <div className="popup-reset-content">
                                            <p>Bạn đã không đạt yêu cầu để hoàn thành video, yêu cầu xem lại.</p>
                                            <button onClick={handlePopupReset}>Đóng</button>
                                        </div>
                                    </div>
                                )}
                                <div className='lecture-content-top'>
                                    <h1>{lectureTitle}</h1>
                                    <button onClick={() => saveTime()}>Lưu tiến trình</button>

                                </div>
                                <div className='referrence'>
                                    <p>Tài liệu tham khảo</p>
                                    <ul>
                                        {lectureMaterial.map((file) => (
                                            <li>
                                                <a href={file.externalLink}>{file.fileName}</a>
                                            </li>
                                        ))}
                                    </ul>
                                    {isCompleted && questionList.length != 0 && (
                                        <div>
                                            <p>Tải bộ câu hỏi</p>
                                            <a
                                                href="#"
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    exportPdfLectureQuestion(
                                                        currentPosition?.id, // ID bài giảng
                                                        {}, // Các params nếu cần
                                                        () => {
                                                            console.log('Download thành công!');
                                                        },
                                                        (error) => {
                                                            console.error('Download thất bại!', error);
                                                        }
                                                    );
                                                }}
                                            >
                                                Tải bộ đề câu hỏi của bài giảng
                                            </a>
                                        </div>
                                    )}
                                </div>
                            </>

                        )}
                        {currentPosition?.type === "TEST_COURSE" && (
                            <TestCourse testInfo={currentPosition}
                                isInitialCompleted={completedTestCourse.includes(currentPosition.id)}
                            ></TestCourse>
                        )}
                    </div>
                </Col>
                <Col xs={24} md={8} lg={6} xl={5}>
                    <div className="course-content-container">
                        <Collapse
                            accordion={false}
                            expandIconPosition="start"
                            className="course-chapters-collapse"
                        >
                            {chapterCourseDetail.sort((a, b) => a.chapterSequence - b.chapterSequence).map((data) => (
                                <Panel
                                    header={data.chapterName}
                                    key={data.chapterId}
                                >
                                    <ul className="lecture-list">
                                        {data.lectureList.sort((a, b) => a.sequence - b.sequence).map((lecture, index) => {
                                            const isLectureCompleted = lectureCompleted.includes(lecture.id);
                                            const isCurrentLecture = currentPosition?.id === lecture.id &&
                                                currentPosition?.type === 'LECTURE';
                                            const isMaxPositionLecture = maxPosition?.type === 'LECTURE' && maxPosition.id === lecture.id;
                                            const isClickablePosition = isLectureCompleted || isCurrentLecture || isMaxPositionLecture ||
                                                (maxPosition?.type === 'LECTURE' && lecture.sequence <= maxPosition.sequence) ||
                                                (maxPosition?.type === 'TEST_COURSE' &&
                                                    lecture.chapterId === maxPosition.chapterId &&
                                                    lecture.sequence <= maxPosition.sequence);

                                            return (
                                                <li
                                                    key={lecture.id}
                                                    className={`${currentPosition?.id === lecture.id ? 'active-lecture' : ''} 
                    ${!isClickablePosition ? 'disabled-lecture' : ''}`}
                                                    onClick={() => {
                                                        if (isClickablePosition) {
                                                            setCurrentPosition({
                                                                id: lecture.id,
                                                                type: 'LECTURE',
                                                                name: lecture.lectureName,
                                                                chapterId: lecture.chapterId
                                                            });
                                                        }
                                                    }}

                                                >
                                                    📘 Bài {index + 1} - {lecture.lectureName}
                                                </li>
                                            );
                                        })}

                                        {data.testCourseDtos.sort((a, b) => a.sequence - b.sequence).map((test, index) => {
                                            const isTestCompleted = completedTestCourse.includes(test.id);
                                            const isCurrentTest = currentPosition?.id === test.id &&
                                                currentPosition?.type === 'TEST_COURSE';
                                            const isMaxPositionTest = maxPosition?.type === 'TEST_COURSE' && maxPosition.id === test.id;

                                            const isClickablePosition = isTestCompleted || isCurrentTest || isMaxPositionTest ||
                                                (maxPosition?.type === 'TEST_COURSE' &&
                                                    test.chapterId === maxPosition.chapterId &&
                                                    test.sequence <= maxPosition.sequence) ||
                                                (maxPosition?.type === 'LECTURE' &&
                                                    test.chapterId === maxPosition.chapterId &&
                                                    test.sequence <= maxPosition.sequence);

                                            return (
                                                <li
                                                    key={test.id}
                                                    className={`${currentPosition?.id === test.id ? 'active-lecture' : ''} 
                    ${!isClickablePosition ? 'disabled-lecture' : ''}`}
                                                    onClick={() => {
                                                        if (isClickablePosition) {
                                                            setCurrentPosition({
                                                                id: test.id,
                                                                type: 'TEST_COURSE',
                                                                name: test.name,
                                                                chapterId: test.chapterId
                                                            });
                                                        }
                                                    }}
                                                >
                                                    📝 {test.name}
                                                </li>
                                            );
                                        })}
                                    </ul>

                                </Panel>
                            ))}
                        </Collapse>

                    </div>
                </Col>
            </Row>
        </div>
    );

}

export default StudentLearning;
