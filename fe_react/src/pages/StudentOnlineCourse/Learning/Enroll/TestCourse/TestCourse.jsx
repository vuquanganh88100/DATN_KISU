import React, { useEffect, useState } from 'react'
import { exportPdfTestCourse, getTestCourseDetail, getTestCourseOverview, getTestResult, submitTestCourse } from '../../../../../services/testCourseService'
import { Button } from 'antd';
import './TestCourse.scss'
import DOMPurify from 'dompurify';

function TestCourse({ testInfo, isInitialCompleted }) {
    console.log(testInfo)
    console.log(isInitialCompleted)
    const [counter, setCounter] = useState(0)
    const [selectedAns, setSelectedAns] = useState({})
    useEffect(() => {
        const timer =
            counter > 0 && setInterval(() => setCounter(counter - 1), 1000);
        return () => clearInterval(timer);
    }, [counter]);
    const formatTime = (seconds) => {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secondsLeft = seconds % 60;

        return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secondsLeft.toString().padStart(2, '0')}`;
    };
    const useCleanHtml = (dirtyHtml) => {
        if (!dirtyHtml) return '';
        return DOMPurify.sanitize(dirtyHtml, {
            ALLOWED_TAGS: ['p', 'br', 'b', 'i', 'em', 'strong', 'u', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
                'ul', 'ol', 'li', 'span', 'div', 'img', 'sub', 'sup'],
            ALLOWED_ATTR: ['src', 'alt', 'style', 'href', 'target', 'rel'],
        });
    };

    const SafeHTMLContent = ({ html, className }) => {
        const cleanHtml = useCleanHtml(html);
        return (
            <div
                className={className}
                dangerouslySetInnerHTML={{ __html: cleanHtml }}
            />
        );
    };
    const [testOverview, setTestOverview] = useState(null);
    const [testCourseDetail, setTestCourseDetail] = useState(null)
    const [isTestStarted, setIsTestStarted] = useState(false)
    const [testCourseQuestion, setTestCourseQuestion] = useState(null)
    const [detailQuestion, setDetailQuestion] = useState()
    const [testResult, setTestResult] = useState(null);
    const [isTestCompleted, setIsTestCompleted] = useState(false);
    useEffect(() => {
        getTestCourseOverview(
            testInfo.id,
            {},
            (responseData) => {
                setTestOverview(responseData);
                console.log(responseData);
            },
            (error) => {
                console.error("Error fetching test course overview:", error);
            }
        );
    }, [testInfo]);
    useEffect(() => {
        if (isInitialCompleted) {
            getTestResult(
                testInfo.id,
                {},
                (responseData) => {
                    setTestResult(responseData);
                    console.log(responseData);
                    setIsTestCompleted(true);

                },
                (error) => {
                    console.error("Error fetching test course overview:", error);
                }
            )
        }
    }, [testInfo], [isInitialCompleted]);
    // chi duoc gọi khi click vào start , vì vậy tránh bị vô hạn vòng lặp
    const startTestCourse = () => {
        getTestCourseDetail(
            testInfo.id,
            {},
            (responseData) => {
                setTestCourseDetail(responseData);
                setTestCourseQuestion(responseData.testCourseQuestionDtos)
                setCounter(responseData.duration)
                if (responseData.testCourseQuestionDtos?.length > 0) {
                    setDetailQuestion(responseData.testCourseQuestionDtos[0]);
                }
                console.log('Test course detail:', responseData);
                setIsTestStarted(true)

            },
            (error) => {
                console.error('Error fetching test course detail:', error);
            }
        );
    };


    if (!testOverview) {
        return <div>Loading...</div>;
    }
    const handClickQuestion = (question) => {
        setDetailQuestion(question)
    }
    const handleAnswerSelect = (questionId, answerId) => {
        setSelectedAns(prev => {
            const currentAnswers = prev[questionId] || [];
            if (detailQuestion.multipleAns) {
                return {
                    ...prev,
                    [questionId]: currentAnswers.includes(answerId)
                        ? currentAnswers.filter(id => id !== answerId)
                        : [...currentAnswers, answerId]
                };
            } else {
                return {
                    ...prev,
                    [questionId]: [answerId]
                };
            }
        });
    };
    console.log(selectedAns)
    const isAnswerSelected = (questionId, answerId) => {
        return selectedAns[questionId]?.includes(answerId);
    };
    // to dam cau hoi da tra loi
    const isQuestionAnswered = (questionId) => {
        return selectedAns[questionId] && selectedAns[questionId].length > 0;
    };

    const onClickSubmitTest = () => {
        const totalTimeTodo = testCourseDetail.duration - counter;

        const resultTestCourseDetailDtoList = testCourseQuestion.map(question => ({
            questionId: question.questionId,
            selectedAnsId: selectedAns[question.questionId]?.map(id => parseInt(id)) || []
        }));
        const payload = {
            testCourseId: testCourseDetail.id,
            resultTestCourseDetailDtoList: resultTestCourseDetailDtoList,
            totalTimeTodo: totalTimeTodo
        };
        submitTestCourse(
            payload,
            (response) => {
                console.log('submit test course successfully')
                setTestResult(response.data)
                console.log(response.data)
                setIsTestCompleted(true)
            },
            (error) => {
                console.log(error)
            }
        )
        console.log(payload)
    }
    function TestResult({ result }) {
        const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
        const currentQuestion = result.resultTestCourseDetailResDtos[currentQuestionIndex];

        const handleClickQuestion = (index) => {
            setCurrentQuestionIndex(index);
        };

        return (
            <div className="test-interface">
                <div className="test-interface-left">
                    <div className="timer">
                        <span>Kết quả: {(result.studentPoint).toFixed(2)}/{(result.testPoint).toFixed(2)}</span>                    </div>
                    <div className="test-course-question">
                        {result.resultTestCourseDetailResDtos.map((question, index) => (
                            <Button
                                key={question.questionId}
                                className={`test-course-question-item 
                                    ${question.correct ? 'correct' : 'incorrect'}
                                `}
                                onClick={() => handleClickQuestion(index)}
                            >
                                <div className="test-course-question-index">{index + 1}</div>
                            </Button>
                        ))}
                    </div>
                </div>
                <div className="test-interface-right">
                    <div className="test-course-question-detail">
                        {currentQuestion && (
                            <div>
                                <div className="test-course-question-detail-content">

                                    <div className='test-course-question-detail-content-title'>
                                        <SafeHTMLContent
                                            html={currentQuestion.questionContent}
                                            className="prose max-w-none text-lg"
                                        />
                                    </div>
                                    <div className="test-course-question-detail-content-answers">
                                        {currentQuestion.answerResDTOS.map((answer, index) => (
                                            <div
                                                key={answer.id}
                                                className={`test-course-question-detail-content-answer 
                                                    ${currentQuestion.selectedAns.includes(answer.id) ? 'selected-answer' : ''}
                                                `}
                                            >
                                                <div className="test-course-question-detail-content-answer-index">
                                                    {String.fromCharCode(65 + index)}
                                                </div>
                                                <div className="test-course-question-detail-content-answer-content">
                                                    <SafeHTMLContent
                                                        html={answer.content}
                                                        className="prose max-w-none flex-1"
                                                    />
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                    <div className="download-section">
                        <Button
                            type="primary"
                            className="download-button"
                            onClick={(e) => {
                                e.preventDefault();
                                exportPdfTestCourse(
                                    testInfo.id, 
                                    {}, 
                                    () => {
                                        console.log('Download thành công!');
                                    },
                                    (error) => {
                                        console.error('Download thất bại!', error);
                                    }
                                );
                            }}                        >
                            <span className="download-icon">📥</span>
                            Download bộ đề kiểm tra
                        </Button>
                    </div>
                </div>
            </div>
        );
    }
    if (isTestCompleted) {
        return <TestResult result={testResult} />;
    }
    if (isTestStarted) {
        return <TestStarted testCourseDetail={testCourseDetail} />;
    }
    function TestStarted() {
        return (
            <div className="test-interface">
                <div className="test-interface-left">
                    <div className="timer">
                        {formatTime(counter)}
                    </div>
                    <div className="test-course-question">
                        {testCourseQuestion.map((question, index) => (
                            <Button
                                key={question.questionId}
                                className={`test-course-question-item ${detailQuestion.questionId === question.questionId ? 'selected' : ''}
                                ${isQuestionAnswered(question.questionId) ? 'answered' : ''}
                                `}
                                onClick={() => handClickQuestion(question)}

                            >
                                <div className="test-course-question-index">{index + 1}</div>
                            </Button>
                        ))}
                    </div>
                    <div className='submit-test'>
                        <Button type='link'
                            onClick={onClickSubmitTest}
                        >
                            Nộp bài thi
                        </Button>
                    </div>
                </div>
                <div className='test-interface-right'>
                    <div className='test-course-question-detail'>
                        {detailQuestion && (
                            <div>
                                <div className='test-course-question-detail-content'>
                                    <div className='test-course-question-detail-content-title'>
                                        <SafeHTMLContent
                                            html={detailQuestion.questionContent}
                                            className="prose max-w-none text-lg"
                                        />
                                    </div>
                                    <div className='test-course-question-detail-content-answers'>
                                        {detailQuestion.answerResDTOList.map((answer, index) => (

                                            <div
                                                key={answer.id}
                                                className='test-course-question-detail-content-answer'
                                                onClick={() => handleAnswerSelect(detailQuestion.questionId, answer.id)}
                                            >
                                                <div className='test-course-question-detail-content-answer-index'>
                                                    {String.fromCharCode(65 + index)}
                                                </div>
                                                <div className='test-course-question-detail-content-answer-content'>
                                                    <label>
                                                        <input
                                                            type={detailQuestion.multipleAns ? 'checkbox' : 'radio'}
                                                            checked={isAnswerSelected(detailQuestion.questionId, answer.id)}
                                                            onChange={() => handleAnswerSelect(detailQuestion.questionId, answer.id)}
                                                            name={`question-${detailQuestion.questionId}`}
                                                        />
                                                        <SafeHTMLContent
                                                            html={answer.content}
                                                            className="prose max-w-none flex-1"
                                                        />
                                                    </label>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        );
    }
    return (
        <div className='student-test-course'>
            <div className='test-course-overview'>
                <div className='test-name'>{testOverview.name}</div>
                <div className='test-duration'>{testOverview.duration / 60} phút</div>
                <div className='test-total-question'>Số lượng câu hỏi: {testOverview.questionQuantity}</div>
                <Button onClick={startTestCourse}>Bắt đầu bài thi</Button>
            </div>
        </div>
    )
}

export default TestCourse