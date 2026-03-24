import { Button, Checkbox, Form, Input, InputNumber, Tag } from 'antd';
import React, { useState } from 'react';
import ReactQuill from 'react-quill';
import './TestCourseAdd.scss';
import { renderTag, tagRender } from '../../utils/tools';
import { testCourseAdd } from '../../services/testCourseService';
import TextArea from 'antd/es/input/TextArea';

function TestCourseAdd({ questionList, courseId, chapterId }) {
    console.log(chapterId)
    const [onlineCourseId, setOnlineCourseId] = useState(Number(courseId));
    const [testName, setTestName] = useState('');
    const [mediumQuestion, setMediumQuestion] = useState(0);
    const [hardQuestion, setHardQuestion] = useState(0);
    const [easyQuestion, setEasyQuestion] = useState(0);
    const [sequence, setSequence] = useState(1);
    const [selectedQuestion, setSelectedQuestion] = useState([]);
    const [duration, setDuration] = useState(0);
    const [chapter, setChapter] = useState(Number(chapterId));
    const [testCourseWeight, setTestCourseWeight] = useState(0)
    const [totalPoint, setTotalPoint] = useState(0)
    const [questionQuantity, setQuestionQuantity] = useState(0)
    const handleCheckboxChange = (id, level) => {
        setSelectedQuestion((prevSelected) => {
            if (prevSelected.includes(id)) {
                const updatedSelected = prevSelected.filter((questionId) => questionId !== id);
                updateQuestionCount(level, 'decrement');
                return updatedSelected;
            } else {
                const updatedSelected = [...prevSelected, id];
                updateQuestionCount(level, 'increment');
                return updatedSelected;
            }
        });
    };

    const updateQuestionCount = (level, action) => {
        if (action === 'increment') {
            // Tăng số lượng câu hỏi theo mức độ
            if (level === 0) {
                setEasyQuestion((prev) => prev + 1);
            } else if (level === 1) {
                setMediumQuestion((prev) => prev + 1);
            } else if (level === 2) {
                setHardQuestion((prev) => prev + 1);
            }
        } else if (action === 'decrement') {
            if (level === 0) {
                setEasyQuestion((prev) => prev - 1);
            } else if (level === 1) {
                setMediumQuestion((prev) => prev - 1);
            } else if (level === 2) {
                setHardQuestion((prev) => prev - 1);
            }
        }

        setQuestionQuantity(easyQuestion + mediumQuestion + hardQuestion);
    };

    console.log(questionQuantity)
    const handleSave = () => {
        const durationInSeconds = duration * 60;

        const formData = {
            onlineCourseId: onlineCourseId,
            name: testName,
            mediumQuestion: mediumQuestion,
            hardQuestion: hardQuestion,
            easyQuestion: easyQuestion,
            questionQuantity: questionQuantity,
            sequence: sequence,
            questionId: selectedQuestion,
            duration: durationInSeconds,
            chapterId: chapter,
            testCourseWeight: testCourseWeight,
            totalPoint: totalPoint,

        };
        console.log('Thông tin trong form:', formData);
        testCourseAdd(
            formData,
            (data) => {
                console.log('Success:', data);
            },
            (error) => {
                console.error('Error:', error);
            }
        );
    };

    return (
        <div className='test-course-add' style={{  justifyContent: 'space-between', gap: '20px' }}>
            <h2>Tạo bài kiểm tra chương</h2>
            <div className='test-course-add-info'>
                <div className='left-column' style={{ flex: 1 }}>
                    <div className='test-name' style={{ marginTop: '20px' }}>
                        <label htmlFor='testName' style={{ fontWeight: 'bold', marginBottom: '8px', display: 'block', color: '#333' }}>Tên bài kiểm tra</label>
                        <TextArea
                            id='testName'
                            value={testName}
                            onChange={(e) => setTestName(e.target.value)}
                            style={{ width: '100%', padding: '10px', fontSize: '14px', borderRadius: '4px', border: '1px solid #ddd' }}
                            autoSize={{ minRows: 8}}
                            />
                    </div>
                </div>

                {/* Cột bên phải */}
                <div className='right-column' style={{ flex: 1 }}>
                    <div className='duration' style={{ marginTop: '20px', marginBottom: '12px' }}>
                        <label htmlFor='duration' style={{ fontWeight: 'bold', marginBottom: '8px', display: 'block', color: '#333' }}>Thời lượng làm bài kiểm tra (phút)</label>
                        <InputNumber
                            id='duration'
                            value={duration}
                            onChange={(value) => setDuration(value)}
                            style={{ width: '100%', padding: '10px', fontSize: '14px', borderRadius: '4px', border: '1px solid #ddd' }}
                        />
                    </div>

                    <div className='total-point' style={{ marginTop: '15px', marginBottom: '12px' }}>
                        <label htmlFor='totalPoint' style={{ fontWeight: 'bold', marginBottom: '8px', display: 'block', color: '#333' }}>Tổng điểm cho bài thi</label>
                        <InputNumber
                            id='totalPoint'
                            value={totalPoint}
                            onChange={(value) => setTotalPoint(value)}
                            style={{ width: '100%', padding: '10px', fontSize: '14px', borderRadius: '4px', border: '1px solid #ddd' }}
                        />
                    </div>

                    <div className='test-course-weight' style={{ marginTop: '15px', marginBottom: '12px' }}>
                        <label htmlFor='testCourseWeight' style={{ fontWeight: 'bold', marginBottom: '8px', display: 'block', color: '#333' }}>Trọng số bài kiểm tra</label>
                        <InputNumber
                            id='testCourseWeight'
                            value={testCourseWeight}
                            onChange={(value) => setTestCourseWeight(value)}
                            style={{ width: '100%', padding: '10px', fontSize: '14px', borderRadius: '4px', border: '1px solid #ddd' }}
                        />
                    </div>
                </div>
            </div>
            <div className='test-course-question'>
                <h3>danh sách câu hỏi</h3>
                {questionList.map((item, index) => (
                    <div className='question-items' key={item.id}>
                        <div className="topic-level">
                            <div className='question-topic'>
                                <div className='question-number'>
                                    <Checkbox
                                        className='checkbox'
                                        checked={selectedQuestion.includes(item.id)}
                                        onChange={() => handleCheckboxChange(item.id, item.level)}
                                    />
                                    {`Câu ${index + 1}`}
                                </div>
                                <ReactQuill
                                    value={item.content}
                                    readOnly={true}
                                    theme="snow"
                                    modules={{ toolbar: false }}
                                />
                            </div>
                            <Tag className="font-bold" color={tagRender(item.level)}>
                                {renderTag(item)}
                            </Tag>
                            {item.lstAnswer &&
                                item.lstAnswer.map((ans, ansNo) => (
                                    <div
                                        className={ans.isCorrect ? 'answer-items corrected' : 'answer-items'}
                                        key={ansNo}
                                    >
                                        <span>{`${String.fromCharCode(65 + ansNo)}. `}</span>
                                        <ReactQuill
                                            value={ans.content}
                                            readOnly={true}
                                            theme="snow"
                                            modules={{ toolbar: false }}
                                        />
                                    </div>
                                ))}
                        </div>
                    </div>
                ))}
            </div>
            <Button type="primary" onClick={handleSave}>
                Lưu
            </Button>
        </div>
    );
}

export default TestCourseAdd;