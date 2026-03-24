import { UploadOutlined } from '@ant-design/icons';
import { Button, Checkbox, Form, Input, InputNumber, Tag, Upload } from 'antd';
import React, { useEffect } from 'react'
import ReactQuill from 'react-quill';
import './LectureEdit.scss'

function LectureEdit({ lecture, questionList }) {
    const selectedQuestion = lecture?.lectureQuestionDtos || [];
    const [form] = Form.useForm();
    const lectureMaterial = lecture?.lectureMaterialDtos || []
    const filteredLectureMaterials = lectureMaterial
    .filter(item => item.type === 1)
    .map(item => {
      const truncatedFileName = item.fileName && item.fileName.length > 20
        ? item.fileName.substring(0, 20) + "..."
        : item.fileName;
  
      return {
        ...item, 
        fileName: truncatedFileName, 
      };
    });
  
  console.log(filteredLectureMaterials);
  
    useEffect(() => {
        if (lecture && lecture.lectureName) {
            form.setFieldsValue({
                lectureName: lecture.lectureName,
                lectureWeight: lecture.lectureWeight,
                videoDuration: lecture.videoDuration,
                requiredTime: lecture.requiredTime,
                requiredCorAns:lecture.requiredCorrectAns
            });
        }
    }, [lecture, form]);

    return (
        <div className="lecture-edit-container">
            <Form form={form}>
                <Form.Item
                    label="Tiêu đề bài giảng"
                    name="lectureName"
                    rules={[{ required: true, message: 'Vui lòng nhập tiêu đề bài giảng!' }]} >
                    <Input readOnly />
                </Form.Item>

                <Form.Item
                    label="Trọng số bài giảng"
                    name="lectureWeight"
                    rules={[{ required: true, message: 'Vui lòng nhập trọng số!' }]} >
                    <InputNumber readOnly  />
                </Form.Item>

                <Form.Item label="Video" className="video-section">
                    <video
                        controls
                        style={{ width: '100%' }}
                        src={lecture?.urlVideo}
                    >
                        Trình duyệt của bạn không hỗ trợ video.
                    </video>
                </Form.Item>
                <Form.Item label="Tài liệu" className='material'>
                    <ul>
                        {filteredLectureMaterials.map((file) => (
                            <a href={file.externalLink}>{file.fileName}</a>
                        ))}
                    </ul>
                </Form.Item>

                <Form.Item
                    label="Thời lượng video"
                    name="videoDuration"
                    rules={[{ required: true, message: 'Vui lòng nhập thời lượng video!' }]} >
                    <InputNumber readOnly  />
                </Form.Item>

                <Form.Item
                    label="Thời lượng xem tối thiểu"
                    name="requiredTime" >
                    <InputNumber min={0} readOnly  />
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
                                        value={item.content}
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
                            <Form.Item
                                label="Thời gian xuất hiện"
                                name={`timeShownUp_${item.id}`}
                                initialValue={item.timeEnd} >
                                <Input readOnly />
                            </Form.Item>

                        </div>
                    ))}
                <Form.Item label="Số câu hỏi cần trả lời đúng" className='requiredCorAns' name="requiredCorAns">
                    <InputNumber readOnly></InputNumber>
                </Form.Item>
                </div>
            </Form>
        </div>
    );
}

export default LectureEdit;
