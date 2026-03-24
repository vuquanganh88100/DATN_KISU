import React, { useEffect } from 'react';
import { Form, Input, InputNumber } from 'antd';
import ReactQuill from 'react-quill';
import './TestCourseEdit.scss';

function TestCourseEdit({ testCourse }) {
    const [form] = Form.useForm();
    const selectedQuestion = testCourse?.testCourseQuestionDtos || [];

    useEffect(() => {
        if (testCourse && testCourse.name) {
            form.setFieldsValue({
                testName: testCourse.name,
                testWeight: testCourse.testCourseWeight,
                duration: testCourse.duration,
                questionQuantity: testCourse.questionQuantity
            });
        }
    }, [testCourse, form]);

    return (
        <div className="test-course-edit-container">
            <Form form={form}>
                <Form.Item
                    label="Tên bài kiểm tra"
                    name="testName"
                    rules={[{ required: true, message: 'Vui lòng nhập tên bài kiểm tra!' }]} >
                    <Input readOnly />
                </Form.Item>

                <Form.Item
                    label="Trọng số bài kiểm tra"
                    name="testWeight"
                    rules={[{ required: true, message: 'Vui lòng nhập trọng số!' }]} >
                    <InputNumber readOnly />
                </Form.Item>

                <Form.Item
                    label="Thời lượng"
                    name="duration"
                    rules={[{ required: true, message: 'Vui lòng nhập thời lượng!' }]} >
                    <InputNumber readOnly />
                </Form.Item>

                <Form.Item
                    label="Số lượng câu hỏi"
                    name="questionQuantity"
                    rules={[{ required: true, message: 'Vui lòng nhập số lượng câu hỏi!' }]} >
                    <InputNumber readOnly />
                </Form.Item>

                <div className='question-container-configure'>
                    <p>Thông tin câu hỏi</p>
                    {selectedQuestion.map((item, index) => (
                        <div key={item.id} className='question-items'>
                            <div className='topic-level'>
                                <div className='question-topic'>
                                    <div className='question-number'>
                                        {`Câu ${index + 1}`}
                                    </div>
                                    <ReactQuill
                                        value={item.questionContent}
                                        readOnly={true}
                                        theme="snow"
                                        modules={{ toolbar: false }}
                                    />
                                </div>
                                {item.answerResDTOList &&
                                    item.answerResDTOList.length > 0 &&
                                    item.answerResDTOList.map((ans, ansNo) => (
                                        <div
                                            className={ans.isCorrect ? 'answer-items corrected' : 'answer-items'}
                                            key={`answer${ansNo}`}
                                        >
                                            <span>{`${String.fromCharCode(65 + ansNo)}. `}</span>
                                            <ReactQuill
                                                key={ansNo}
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
            </Form>
        </div>
    );
}

export default TestCourseEdit;