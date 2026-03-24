import React, { useEffect, useState } from 'react'
import "./OnlineCourseAdd.scss";
import useCombo from '../../../hooks/useCombo';
import { Button, Checkbox, Select, Form, Input, InputNumber } from "antd";
import { useDropzone } from 'react-dropzone';
import { addOnlineCourse, editOnlineCourseById, getOnlineCourseById } from '../../../services/onlineCourseService';
import useNotify from '../../../hooks/useNotify';
import { useParams } from 'react-router-dom';
import ReactPlayer from 'react-player';
import ReactQuill from 'react-quill';
export default function OnlineCourseAdd() {

    const {
        getAllSubjects,
        getAllTeacher,
        getAllChapters,
        allSubjects,
        allTeacher,
        allChapters

    } = useCombo();
    const notify = useNotify();
    // handle useState
    const [subjectId, setSubjectId] = useState();
    const [teacherId, setTeacherId] = useState([]);
    const [file, setFile] = useState();
    const [previewImageURL, setPreviewImageURL] = useState();
    const [step, setStep] = useState(1)
    const [courseDescription, setCourseDescription] = useState("")
    const [courseName, setCourseName] = useState("")
    const [chapter, setChapter] = useState([])
    const [form] = Form.useForm();

    const course = {
        fileCourseImg: file,
        subjectId: subjectId,
        courseName: courseName,
        courseDescription: courseDescription,
        teacherId: teacherId,
        onlineCourseChaptersDto: chapter
    };    // console.log(chapter)

    // get id from path to access information specific course
    const courseId = useParams()
    console.log(courseId.id)
    // handle dropzone
    const onDrop = (acceptedFile) => {
        setFile(acceptedFile[0])
        setPreviewImageURL(URL.createObjectURL(acceptedFile[0]))
    }
    const { getRootProps, getInputProps } = useDropzone({
        maxFiles: 1,
        onDrop
    });

    // handle useEffect
    useEffect(() => {
        console.log({ subjectId, teacherId, file, previewImageURL, step, courseDescription, courseName });
    }, [subjectId, teacherId, file, previewImageURL, step, courseDescription, courseName]);

    useEffect(() => {
        getAllSubjects({ subjectCode: null, subjectTitle: null });
        getAllTeacher({ teacherName: null, teacherCode: null })
    }, [])
    useEffect(() => {
        setChapter([]);
    }, []);
    useEffect(() => {
        if (subjectId) {
            getAllChapters({
                subjectId: subjectId,
                chapterCode: null,
                chapterId: null,
            });
        }

    }, [subjectId]);
    useEffect(() => {
        console.log(course)

        if (step === 1) {
            form.setFieldsValue(
                {
                    subject: subjectId,
                    courseName: courseName,
                    courseDescription: courseDescription,
                    teacher: teacherId
                }
            )
        } else if (step === 2) {
            const chapterValues = {};
            chapter.forEach((chapterItem) => {
                chapterValues[`chapter${chapterItem.chapterId}`] = chapterItem.chapterWeight;
            });
            form.setFieldsValue(chapterValues); // Cập nhật giá trị vào form
        }
    }, [step, chapter])
    useEffect(() => {
        if (!courseId.id) { // Nếu không có courseId, reset các giá trị về mặc định (null hoặc empty)
            form.resetFields(); // Reset các trường trong form
            setSubjectId(null); // Reset state subjectId
            setTeacherId([]); // Reset state teacherId
            setCourseName(""); // Reset state courseName
            setCourseDescription(""); // Reset state courseDescription
            setChapter([]); // Reset state chapter
            setPreviewImageURL(null); // Reset state previewImageURL
        } else {
            // Nếu có courseId, gọi API để tải dữ liệu khóa học
            getOnlineCourseById(
                courseId.id,
                {},
                (course) => {
                    form.setFieldsValue({
                        subject: course.subjectId,
                        courseName: course.courseName,
                        courseDescription: course.courseDescription,
                        teacher: course.teacherId,
                        previewImageURL: course.courseUrlImg

                    });
                    setSubjectId(course.subjectId);
                    setTeacherId(course.teacherId);
                    setCourseName(course.courseName);
                    setCourseDescription(course.courseDescription);
                    setPreviewImageURL(course.courseUrlImg)

                    // Cập nhật chapters nếu có
                    if (course.onlineCourseChaptersDto) {
                        setChapter(course.onlineCourseChaptersDto);
                        const chapterValues = {};
                        course.onlineCourseChaptersDto.forEach(chapterItem => {
                            chapterValues[`chapter${chapterItem.chapterId}`] = chapterItem.chapterWeight;
                        });
                        form.setFieldsValue(chapterValues); // Cập nhật giá trị vào form
                    }

                    if (course.fileCourseImg) {
                        setPreviewImageURL(course.fileCourseImg);
                    }
                },
                (error) => {
                    console.error("Lỗi khi lấy khóa học:", error);
                    notify.error("Không thể tải dữ liệu khóa học");
                }
            );
        }
    }, [courseId]);

    // handle OnChange
    const subjectOnChange = (value) => {
        console.log(value)
        setSubjectId(value)
    };
    const teacherOnChange = (value) => {
        console.log(value)
        setTeacherId(value)
    }
    const courseNameOnChange = (e) => {
        setCourseName(e.target.value)
    }
    const courseDescriptionOnChange = (value) => {
        setCourseDescription(value)
    }

    const handleChapterWeightChange = (chapterId, chapterWeight) => {
        setChapter((prev) => {
            const index = prev.findIndex((item) => item.chapterId === chapterId);
            if (index !== -1) {
                // Nếu chapterId đã tồn tại, cập nhật chapterWeight
                const updatedChapters = [...prev];
                updatedChapters[index].chapterWeight = chapterWeight;
                return updatedChapters;
            }
            // Nếu chapterId chưa tồn tại, thêm mới
            return [...prev, { chapterId, chapterWeight }];
        });
    };

    // console.log(allChapters)


    //handle OnClick
    const nextStep = () => {
        setStep(step + 1)
        console.log('next')
    }
    const previousStep = () => {
        setStep(step - 1)
    }

    // xử lý file dựa vào url từ db
    const fetchFileFromUrl = async (url, fileName) => {
        const response = await fetch(url);
        const blob = await response.blob(); // Chuyển thành Blob
        const file = new File([blob], fileName, { type: blob.type });
        return file;
    };
    useEffect(() => {
        const loadFileFromUrl = async () => {
            const url = previewImageURL;
            const fileName = "course_image.jpg";
            try {
                const fetchedFile = await fetchFileFromUrl(url, fileName);
                setFile(fetchedFile);
            } catch (error) {
                console.error("Error fetching file from URL:", error);
            }
        };

        if (previewImageURL && !file) { // Chỉ tải nếu chưa có file
            loadFileFromUrl();
        }
    }, [previewImageURL, file]);

    const submit = () => {
        const formData = new FormData();
        formData.append("fileCourseImg", file);
        formData.append("subjectId", subjectId);
        formData.append("courseName", courseName);
        formData.append("courseDescription", courseDescription);
        teacherId.forEach(id => formData.append("teacherId", id));

        formData.append("onlineCourseChaptersDto", JSON.stringify(chapter));
        if (!courseId.id) {
            addOnlineCourse(
                formData,
                () => {
                    console.log("Successfully added online course.");
                    notify.success("Thêm mới khóa học thành công")
                },
                (error) => {
                    console.log("Error adding course:", error);
                    notify.error("Lỗi thêm mới khóa học")
                }
            );
        } else {
            editOnlineCourseById(
                courseId.id,
                formData, () => {
                    console.log("Update successfully")
                    notify.success("Cập nhật khóahọc thành công")
                },
                (error) => {
                    console.log("Error adding course:", error);
                    notify.error("Lỗi thêm mới khóa học")
                }
            )
        }

    };




    const multiStep = () => {
        switch (step) {
            case 1:
                return (
                    <Form form={form} className='info-course-form'>
                        <div className='info-course-form-header'>Thông tin khóa học </div>
                        <div className='form-content-wrapper'>
                            <div className='form-left-section'>
                                <Form.Item name="subject" label="Học phần" >
                                    <Select
                                        value={subjectId}
                                        placeholder="Chọn học phần cho khóa học"
                                        onChange={subjectOnChange}
                                    >
                                        {allSubjects.map((subject) => (
                                            <Select.Option key={subject.id} value={subject.id}>
                                                {subject.name}
                                            </Select.Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                                <Form.Item name="courseName" label="Nhập tên khóa học">
                                    <Input
                                        placeholder="Nhập tên khóa học"
                                        value={courseName}
                                        onChange={courseNameOnChange}
                                    />
                                </Form.Item>
                                <Form.Item label="Giáo viên giảng dậy">
                                    <Select
                                        value={teacherId}
                                        placeholder="Chọn giáo viên giảng dạy"
                                        onChange={teacherOnChange}
                                        mode='multiple'
                                        dropdownStyle={{ maxHeight: 200, overflowY: 'auto' }}
                                    >
                                        {allTeacher.map((teacher) => (
                                            <Select.Option value={teacher.id} key={teacher.code}>
                                                {teacher.name}
                                            </Select.Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                                <Form.Item label="Tải lên ảnh khóa học" className='preview-image'>
                            <div {...getRootProps()} className="dropzone">
                                <input {...getInputProps()} />
                                {previewImageURL ? (
                                    <div className="preview-container">
                                        <img
                                            src={previewImageURL}
                                            alt="Xem trước ảnh"
                                        />
                                    </div>
                                ) : (
                                    <p>Kéo hoặc thả ảnh vào đây</p>
                                )}
                            </div>
                            {file && <p className="file-name">{file.name}</p>}
                        </Form.Item>
                            </div>
                            <div className='form-right-section'>
                                <Form.Item name="courseDescription" >
                                    <ReactQuill
                                        placeholder="Mô tả khóa học"
                                        value={courseDescription}
                                        onChange={courseDescriptionOnChange}
                                        rows={6}
                                    />
                                </Form.Item>
                            </div>
                        </div>
                        <Button onClick={nextStep}
                            type="primary"
                            style={{ width: 150, height: 50 }}
                            className='button-next'>Sau</Button>
                    </Form>

                )
            case 2:
                return (
                    <div className='chapterWeight'>
                        <Form
                            form={form}
                            initialValues={chapter.reduce((acc, chapterItem) => {
                                acc[`chapter${chapterItem.chapterId}`] = chapterItem.chapterWeight;
                                return acc;
                            }, {})}
                        >
                            <div className='chapterWeight-header'>
                                <p>Cấu hình trọng số cho chương</p>
                            </div>
                            <div className="chapter-grid">
                                {allChapters.map((chapterItem) => (
                                    <Form.Item
                                        label={`Chương ${chapterItem.name}`}
                                        key={chapterItem.id}
                                        name={`chapter${chapterItem.id}`}
                                    >
                                        <InputNumber
                                            min={0}
                                            max={100}
                                            placeholder="Nhập trọng số (%)"
                                            style={{ width: '100%' }}
                                            value={form.getFieldValue(`chapter${chapterItem.id}`)} // Đồng bộ với form
                                            onChange={(value) =>
                                                handleChapterWeightChange(chapterItem.id, value)
                                            }
                                        />
                                    </Form.Item>
                                ))}
                            </div>
                        </Form>
                        <div className='button-chapter'>
                            <Button onClick={previousStep}
                                type="primary"
                                style={{ width: 150, height: 50 }}
                                className='button-chapter-prev'>
                                Trước
                            </Button>
                            <Button onClick={submit}
                                type="primary"
                                style={{ width: 150, height: 50 }}
                                className='button-chapter-next'>
                                Thêm
                            </Button>
                        </div>
                    </div>
                );


        }
    }
    return (
        <div className='online-course-add'>
            <div className='online-course-add-header'>
                <p>{courseId.id ? "Cập nhật khóa học" : "Tạo khóa học"}</p>
            </div>
            <div>
                {multiStep()}
            </div>
            <div>

            </div>
        </div>
    )
}
