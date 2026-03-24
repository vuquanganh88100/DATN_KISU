import React, { useState, useEffect } from 'react';
import './LectureAdd.scss';
import { Collapse, Form, Input, Button, InputNumber, Tag, Checkbox, Upload, message, Switch, Modal } from 'antd';
import { DeleteFilled, DeleteOutlined, EditOutlined, PlusCircleOutlined, UploadOutlined } from '@ant-design/icons'; // Import icon dấu cộng
import useCombo from '../../../hooks/useCombo';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { getQuestionService } from '../../../services/questionServices';
import ReactQuill from 'react-quill';
import { renderTag, tagRender } from '../../../utils/tools';
import { getCourseDetail, getListLecture, uploadFileAttachment, uploadLecture, uploadVideo } from '../../../services/lectureService';
import useNotify from '../../../hooks/useNotify';
import { updatePublish } from '../../../services/onlineCourseService';
import LectureEdit from '../LecutureEdit/LectureEdit';
import TestCourseAdd from '../../TestCourse/TestCourseAdd';
import TestCourseEdit from '../../TestCourse/TestCourseEdit/TestCourseEdit';
import ModalPopup from '../../../components/ModalPopup/ModalPopup';
import deletePopUpIcon from "../../../assets/images/svg/delete-popup-icon.svg";

const { Panel } = Collapse;

function LectureAdd() {
    const [form] = Form.useForm();
    const { courseId } = useParams();
    const location = useLocation()
    const notify = useNotify();
    const [chapterId, setChapterId] = useState();
    const [questionConfig, setQuestionConfig] = useState(false)
    const [listLecture, setListLecture] = useState([])
    //  toggle collapse




    // lecture data for submit 
    const [lectureMaterialDtos, setLectureMaterialDtos] = useState([]);
    const [lectureQuestionDtos, setLectureQuestionDtos] = useState([])
    const [lectureName, setLectureName] = useState()
    const [requiredTime, setRequiredTime] = useState(0)
    const [lectureWeight, setLectureWeight] = useState()
    const [requiredCorrectAns, setRequiredCorrectAns] = useState()
    const [videoDuration, setVideoDuration] = useState(0);

    const [activeKeys, setActiveKeys] = useState([]); // Quản lý các panel được mở
    const [showInput, setShowInput] = useState({}); // Lưu trạng thái hiển thị input theo từng chapter
    const [selectedQuestion, setSelectedQuestion] = useState([]);

    const lectureData = {
        lectureName: lectureName,
        requiredTime: requiredTime,
        lectureWeight: lectureWeight,
        totalQuestion: selectedQuestion.length,
        requiredCorrectAns: requiredCorrectAns,
        videoDuration: Math.floor(videoDuration),
        chapterId: parseInt(chapterId, 10),
        onlineCourseId: parseInt(courseId, 10),
        lectureMaterialDtos: lectureMaterialDtos,
        lectureQuestionDtos: lectureQuestionDtos
    }

    // get question by chapter
    const [questionList, setQuestionList] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchQuestions = () => {
        setLoading(true);
        getQuestionService(
            null,
            null,
            null,
            chapterId,
            null,
            "",
            null,
            null,
            (res) => {
                console.log(res.data)
                setQuestionList(res.data?.questions || []);
                setLoading(false);
            },
            (error) => {
                console.error(error);
                setLoading(false);
            }
        );
    };
    useEffect(() => {
        fetchQuestions(); // Gọi lại API khi chapterId thay đổi
    }, [chapterId]);

    useEffect(() => {
        console.log(questionList); // Kiểm tra danh sách câu hỏi khi chapterId thay đổi
    }, [questionList]);

    // handle Video

    // Hàm onChange khi upload file
    const onChangeVideo = (info) => {
        if (info.file.status === 'done') {
            message.success(`${info.file.name} file uploaded successfully`);
        } else if (info.file.status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    // Hàm kiểm tra file trước khi upload
    const handleBeforeUpload = (file) => {
        const isVideo = file.type.startsWith('video/');
        if (!isVideo) {
            message.error('Chỉ được phép tải lên video!');
            return false; // Nếu không phải video, không cho phép upload
        }

        // Nếu là video, tiếp tục kiểm tra thời gian video
        handleVideoUpload(file);

        return isVideo;
    };

    const handleVideoUpload = (file) => {
        const videoElement = document.createElement('video');
        videoElement.preload = 'metadata';

        videoElement.addEventListener('loadedmetadata', () => {
            const duration = Math.round(videoElement.duration);
            setVideoDuration(duration);
            form.setFieldsValue({ videoDuration: duration });
            URL.revokeObjectURL(videoElement.src);
            console.log('Video Duration:', duration);
        });

        videoElement.src = URL.createObjectURL(file);
    };

    const customRequest = ({ file, onSuccess, onError }) => {
        const formData = new FormData();
        formData.append('file', file);

        uploadVideo(formData, (response) => {
            console.log('Video uploaded successfully', response);
            onSuccess(response);
            const fileData = [response.data].map((file) => ({
                id: file.id,
                type: file.type,
                fileEx: file.fileEx,
                size: file.size,
                storedType: file.storedType,
                filePath: file.filePath,
                externalLink: file.externalLink || '',
                createdBy: 1,
            }));
            console.log(fileData)
            setLectureMaterialDtos((prev) => [...prev, ...fileData]);

        }, (error) => {
            console.error('Upload failed', error);
            onError(error);
        });
    };


    // handle file attachment
    const [formData, setFormData] = useState(new FormData());

    const onChangeFileAttachment = (info) => {
        const { fileList } = info; // Lấy danh sách file từ info.fileList
        if (!fileList || fileList.length === 0) {
            message.error("Vui lòng chọn file để tải lên");
            return;
        }

        console.log(fileList);

        // Tạo FormData mới mỗi lần chọn file mới
        const newFormData = new FormData();

        // Tạo FormData từ danh sách file
        fileList.forEach(({ originFileObj }) => {
            if (originFileObj) {
                newFormData.append("file", originFileObj);
            }
        });

        setFormData(newFormData);
    };

    const onClickSubmitFile = () => {
        const newFormData = new FormData();

        for (let pair of formData.entries()) {
            newFormData.append(pair[0], pair[1]);
        }

        console.log(newFormData);

        // Gọi API upload
        uploadFileAttachment(
            newFormData,
            (response) => {
                message.success("Tải lên thành công!");
                console.log("Tải lên thành công:", response);
                const uploadedData = response.data;
                const fileData = response.data.map((file) => ({
                    id: file.id,
                    type: file.file,
                    fileEx: file.fileEx,
                    size: file.size,
                    storedType: file.storedType,
                    filePath: file.filePath,
                    externalLink: file.externalLink || '',
                    createdBy: 1,
                }));
                setLectureMaterialDtos((prev) => [...prev, ...fileData]);
            },
            (error) => {
                message.error("Lỗi khi tải lên!");
                console.error("Lỗi khi tải lên:", error);
            }
        );
    };

    // handle lectureQuestionDtos

    const handleCheckboxChange = (id) => {
        setSelectedQuestion((prev) => {
            if (prev.includes(id)) {
                // if question id is exist , remove 
                return prev.filter((selectedId) => selectedId !== id);
            } else {
                return [...prev, id];
            }
        });
    }
    const handleTimeChange = (questionId, timeStart, timeEnd) => {
        // Kiểm tra xem câu hỏi đã có trong mảng hay chưa
        const updatedDtos = [...lectureQuestionDtos];
        const existingIndex = updatedDtos.findIndex(dto => dto.questionId === questionId);

        if (existingIndex >= 0) {
            // Nếu câu hỏi đã tồn tại, cập nhật thông tin thời gian
            updatedDtos[existingIndex] = { questionId, timeStart, timeEnd };
        } else {
            // Nếu câu hỏi chưa có, thêm mới
            updatedDtos.push({ timeStart, timeEnd, questionId });
        }

        setLectureQuestionDtos(updatedDtos);
    };

    const onClickSubmitForm = () => {

        console.log(lectureData)
        uploadLecture(
            lectureData,
            () => {
                notify.success("Thêm mới bài giảng thành công")
            },
            (error) => {
                console.log(error)
                notify.error("Lỗi thêm mới bài giảng")
            }
        )
    }

    // update publish
    const { publish } = location.state
    const [newPublished, setNewPublished] = useState(publish)
    const publishChange = (checked) => {
        setNewPublished(checked)
        updatePublish(courseId, checked,
            () => {
                notify.success("Xuất bản khóa học thành công")
            },
            () => {
                notify.error("Lỗi xuất bản khóa học")
            }
        )
    }
    // refreactor code
    const [courseDetail, setCourseDetail] = useState([]);
    useEffect(() => {
        console.log(courseId)
        getCourseDetail(
            courseId,
            {},
            (data) => {
                setCourseDetail(data);
            },
            (error) => {
                console.log(error);
            }
        );
    }, [courseId]);
    const navigate = useNavigate()
    // handle edit/view lecture form
    const [showLectureForm, setShowLectureForm] = useState(false);
    const [editLecture, setEditLecture] = useState(null); // Store the lecture being edited


    const toggleLectureForm = (lecture) => {
        console.log(lecture);
        if (lecture == null) {
            setShowLectureForm(!showLectureForm);
            setEditLecture(null); // Reset the editLecture state when no lecture is passed
        } else {
            setEditLecture(lecture); // Set the lecture to be edited
        }
    };

    // handle edit/view testCourse form
    const [showTestCourseForm, setShowTestCourseForm] = useState(false);
    const [editTestcourse, setEditTestCourse] = useState(null)
    console.log("test course" + editTestcourse)
    const toggleTestCourseForm = (test) => {
        console.log(test);
        if (test == null) {
            setShowTestCourseForm(!showTestCourseForm);
            setEditTestCourse(null); // Reset the editLecture state when no lecture is passed
        } else {
            setEditTestCourse(test); // Set the lecture to be edited
        }
    };
    const handleCollapseChange = (key) => {
        console.log(key)
        setActiveKeys(key);
        setChapterId(key[0])
    }

    // // toggle testCourseForm
    // const [showTestCourseForm, setShowTestCourseForm] = useState(false);

    // const toggleTestCourseForm = () => {
    //     setShowTestCourseForm(!showTestCourseForm)
    // }
    return (
        <div className="lecture-add">
            <div className="lecture-add-header">
                <p>Tạo bài giảng cho khóa học</p>
                <Switch
                    checked={newPublished}
                    checkedChildren="Xuất bản"
                    unCheckedChildren="Chưa xuất bản"
                    onChange={publishChange}
                />
            </div>
            <Form form={form}
                className="info-lecture-form"
                layout='vertical'>
                <div className="chapter">
                    <Collapse
                        accordion={true}
                        activeKey={activeKeys}
                        onChange={handleCollapseChange}
                        expandIconPosition="start"
                    >
                        {courseDetail.map((course, index) => (
                            <Panel header={<span style={{
                                fontWeight: '300px',
                                fontSize: '22px'
                            }}>{course.chapterName}</span>} key={course.chapterId}>

                                <ul className='lecture-list'>
                                    {course.lectureList.map((lecture) => (
                                        <li key={lecture.id} className="lecture-item">
                                            <div className="lecture-name">
                                                <span className="lecture-icon">📘</span>
                                                <span>Bài {lecture.sequence}: {lecture.lectureName}</span>
                                            </div>
                                            <div className="lecture-weight">
                                                Trọng số: {lecture.lectureWeight || 0}%
                                            </div>
                                            <div className="lecture-actions">
                                                <Button
                                                    icon={<EditOutlined />}
                                                    onClick={() => toggleLectureForm(lecture)}
                                                >
                                                    Sửa
                                                </Button>
                                                <ModalPopup
                                                    buttonOpenModal={
                                                        <Button
                                                            className="ant-btn ant-btn-last"
                                                            icon={<DeleteOutlined />}
                                                        />
                                                    }
                                                    onAccept={() => { }}
                                                    title="Xóa bài giảng"
                                                    message="Bạn có chắc chắn muốn xóa bài giảng này ? "
                                                    confirmMessage="Hành động này không thể hoàn tác"
                                                    icon={deletePopUpIcon}
                                                    ok="Đồng ý"
                                                />
                                            </div>
                                            <Modal
                                                className="lecture-edit-modal"
                                                title="Thông tin bài giảng"
                                                visible={editLecture === lecture}
                                                onCancel={() => setEditLecture(null)}
                                                footer={null}
                                                style={{ width: '800' }}
                                                bodyStyle={{ maxHeight: '600px', overflowY: 'auto' }}
                                            >
                                                {editLecture && editLecture.id === lecture.id && (
                                                    <LectureEdit lecture={lecture} questionList={questionList} />
                                                )}
                                            </Modal>
                                        </li>
                                    ))}
                                    {course.testCourseDtos.map((test) => (
                                        <li key={test.id} className="lecture-item">
                                            <div className="lecture-name">
                                                <span className="lecture-icon">📝</span>
                                                <span>Bài kiểm tra: {test.name}</span>
                                            </div>
                                            <div className="lecture-weight">
                                                Trọng số: {test.testCourseWeight || 0}%
                                            </div>
                                            <div className="lecture-actions">
                                                <Button
                                                    icon={<EditOutlined />}
                                                    onClick={() => toggleTestCourseForm(test)}
                                                >
                                                    Sửa
                                                </Button>
                                                <ModalPopup
                                                    buttonOpenModal={
                                                        <Button
                                                            className="ant-btn ant-btn-last"
                                                            icon={<DeleteOutlined />}
                                                        />
                                                    }
                                                    onAccept={() => { }}
                                                    title="Xóa bài kiểm tra chương"
                                                    message="Bạn có chắc chắn muốn xóa bài kiểm tra chương này ? "
                                                    confirmMessage="Hành động này không thể hoàn tác"
                                                    icon={deletePopUpIcon}
                                                    ok="Đồng ý"
                                                />
                                            </div>
                                            <Modal
                                                className="test-edit-modal"
                                                title="Thông tin bài kiểm tra chương"
                                                visible={editTestcourse === test}
                                                onCancel={() => setEditTestCourse(null)}
                                                footer={null}
                                                style={{ width: '800' }}
                                                bodyStyle={{ maxHeight: '600px', overflowY: 'auto' }}
                                            >
                                                {editTestcourse && editTestcourse.id === test.id && (
                                                    <TestCourseEdit testCourse={test} />
                                                )}
                                            </Modal>
                                        </li>

                                    ))}
                                </ul>
                                <Button
                                    icon={<PlusCircleOutlined />}
                                    className='button-add-lecture'
                                    onClick={() => toggleLectureForm(null)
                                    }
                                >
                                    Thêm bài giảng
                                </Button>
                                {showLectureForm && (
                                    <div className='add-lecture-form'
                                        style={{ marginTop: "20px" }}>
                                        <h2>Tạo bài giảng</h2>
                                        <div className='add-lecture-info'>
                                            <div className='left-column'>
                                                <Form.Item
                                                    className='lecture-name'
                                                    label="Tiêu đề bài giảng"
                                                    name="lectureName"
                                                    rules={[{ required: true, message: 'Please input your username!' }]}>
                                                    <Input.TextArea
                                                        onChange={(e) => {
                                                            setLectureName(e.target.value);
                                                        }}
                                                        rows={2}
                                                    />
                                                </Form.Item>
                                                <Form.Item label="Trọng số bài giảng"
                                                    name="lectureWeight"
                                                    rules={[{ required: true, message: 'Please input your username!' }]}>
                                                    <InputNumber
                                                        value={lectureWeight}
                                                        onChange={(value) => {
                                                            setLectureWeight(value)
                                                        }}>
                                                    </InputNumber>
                                                </Form.Item>
                                                <Form.Item
                                                    label="Thời lượng video"
                                                    className="video-duration"
                                                    name="videoDuration"
                                                    rules={[{ required: true, message: 'Please input your username!' }]}
                                                >
                                                    <InputNumber disabled />
                                                </Form.Item>

                                                <Form.Item label="Thời lượng xem tối thiểu"
                                                    className='required-time'>
                                                    <InputNumber
                                                        value={requiredTime}
                                                        onChange={(value) => {
                                                            if (value !== null && value !== undefined) {
                                                                setRequiredTime(value);
                                                            }
                                                        }}
                                                    />
                                                </Form.Item>
                                            </div>
                                            <div className='right-column'
                                                style={{ marginTop: 10 }}>
                                                <Form.Item
                                                    className='upload-video'
                                                    name="uploadVideo"
                                                    rules={[{ required: true, message: 'Please input your username!' }]}>
                                                    <Upload
                                                        beforeUpload={handleBeforeUpload}
                                                        onChange={onChangeVideo}
                                                        customRequest={customRequest}
                                                    >
                                                        <Button icon={<UploadOutlined />}> <span style={{ color: 'red' }}>*</span>Tải video lên</Button>
                                                    </Upload>
                                                </Form.Item>
                                                <Form.Item
                                                    className='file-attachment'>
                                                    <Upload
                                                        multiple={true}
                                                        accept=".image/*,.pdf,.pptx,.zip"
                                                        onChange={onChangeFileAttachment}
                                                        beforeUpload={() => false}
                                                    >
                                                        <Button icon={<UploadOutlined />}>Đính kèm tài liệu</Button>
                                                    </Upload>
                                                    <Button onClick={onClickSubmitFile}
                                                        style={
                                                            { marginTop: '10px' }
                                                        }>Lưu file</Button>
                                                </Form.Item>
                                            </div>
                                        </div>
                                        <div className='question-container'>
                                            {/* Nút điều khiển hiển thị cấu hình câu hỏi */}
                                            <Button
                                                className='button-add-question'
                                                onClick={() => setQuestionConfig(!questionConfig)}
                                                style={{ marginBottom: '20px' }}
                                                icon={<PlusCircleOutlined></PlusCircleOutlined>}
                                            >
                                                {questionConfig ? 'Ẩn cấu hình câu hỏi' : 'Thêm câu hỏi cho bài giảng'}
                                            </Button>

                                            {/* Hiển thị danh sách cấu hình câu hỏi nếu questionConfig = true */}
                                            {questionConfig && (
                                                <div className="question-container-configure">
                                                    {questionList.map((item, index) => (
                                                        <div className='question-items'>
                                                            <div className="topic-level" key={item.id}>
                                                                <div className='question-topic'>
                                                                    <div className='question-number'>
                                                                        {/* Checkbox để chọn câu hỏi */}
                                                                        <Checkbox
                                                                            className='checkbox'
                                                                            checked={selectedQuestion.includes(item.id)}
                                                                            onChange={() => handleCheckboxChange(item.id)}
                                                                        />
                                                                        {`Câu ${index + 1}`}
                                                                    </div>
                                                                    {/* Nội dung câu hỏi */}
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
                                                                {/* Danh sách đáp án */}
                                                                {item.lstAnswer &&
                                                                    item.lstAnswer.length > 0 &&
                                                                    item.lstAnswer.map((ans, ansNo) => (
                                                                        <div
                                                                            className={
                                                                                ans.isCorrect ? 'answer-items corrected' : 'answer-items'
                                                                            }
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

                                                                {/* Cấu hình thời gian bắt đầu và kết thúc cho câu hỏi */}
                                                                {selectedQuestion.includes(item.id) && (
                                                                    <div>
                                                                        <Form.Item label="Thời gian bắt đầu">
                                                                            <InputNumber
                                                                                value={
                                                                                    lectureQuestionDtos.find(
                                                                                        (dto) => dto.questionId === item.id
                                                                                    )?.timeStart
                                                                                }
                                                                                onChange={(value) =>
                                                                                    handleTimeChange(
                                                                                        item.id,
                                                                                        value,
                                                                                        lectureQuestionDtos.find(
                                                                                            (dto) => dto.questionId === item.id
                                                                                        )?.timeEnd
                                                                                    )
                                                                                }
                                                                            />
                                                                        </Form.Item>
                                                                        <Form.Item label="Thời gian kết thúc">
                                                                            <InputNumber
                                                                                value={
                                                                                    lectureQuestionDtos.find(
                                                                                        (dto) => dto.questionId === item.id
                                                                                    )?.timeEnd
                                                                                }
                                                                                onChange={(value) =>
                                                                                    handleTimeChange(
                                                                                        item.id,
                                                                                        lectureQuestionDtos.find(
                                                                                            (dto) => dto.questionId === item.id
                                                                                        )?.timeStart,
                                                                                        value
                                                                                    )
                                                                                }
                                                                            />
                                                                        </Form.Item>
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    ))}

                                                    {/* Nhập số câu trả lời đúng tối thiểu */}
                                                    {selectedQuestion.length > 0 && (
                                                        <Form.Item label="Số câu trả lời đúng tối thiểu">
                                                            <InputNumber
                                                                value={requiredCorrectAns}
                                                                onChange={(value) => setRequiredCorrectAns(value)}
                                                            />
                                                        </Form.Item>
                                                    )}
                                                </div>
                                            )}
                                        </div>

                                        {/* Save button */}
                                        <Button
                                            type="default"
                                            style={{ width: 150, height: 50 }}
                                            onClick={onClickSubmitForm}
                                        >
                                            Lưu
                                        </Button>
                                    </div>
                                )}
                                <Button
                                    icon={<PlusCircleOutlined />}
                                    className='button-add-test-course'
                                    style={{ display: "block", marginTop: "20px", marginBottom: "20px" }} // Đảm bảo nút chiếm cả dòng

                                    onClick={() => toggleTestCourseForm(null)
                                    }
                                >
                                    Thêm bài kiểm tra chương
                                </Button>
                                {showTestCourseForm && (
                                    <TestCourseAdd questionList={questionList} courseId={courseId} chapterId={chapterId} />
                                )}
                            </Panel>
                        ))}
                    </Collapse>
                </div>
            </Form>
        </div>
    );
}

export default LectureAdd;
