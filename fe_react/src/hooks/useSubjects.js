import {useState} from "react";
import {
    getChaptersService,
    getPagingSubjectsService,
    getSubjectByCodeService,
} from "../services/subjectsService";
import useNotify from "./useNotify";

const useSubjects = () => {
    const notify = useNotify();
    const [allSubjects, setAllSubjects] = useState([]);
    const [tableLoading, setTableLoading] = useState(true);
    const [pagination, setPagination] = useState({});
    const [subjectInfo, setSubjectInfo] = useState({});
    const [infoLoading, setInfoLoading] = useState(true);
    const [allChapters, setAllChapters] = useState([]);
    const [chapterLoading, setChapterLoading] = useState(false);

    const getAllSubjects = (payload) => {
        setTableLoading(true);
        getPagingSubjectsService(
            payload.search,
            payload.page,
            payload.size,
            payload.sort,
            (res) => {
                setAllSubjects(res?.data?.content);
                setPagination({
                    current: res?.data.pageable.pageNumber + 1,
                    pageSize: res?.data.pageable.pageSize,
                    total: res?.data.totalElements,
                });
                setTableLoading(false);
            },
            (err) => {
                setTableLoading(false);
                if (err?.response?.status === 404) {
                    notify.warning(
                        err?.response?.data?.message ||
                        "No information in database"
                    );
                }
            }
        ).then(() => {});
    };
    const getSubjectByCode = (payload = {}, code) => {
        setInfoLoading(true);
        getSubjectByCodeService(
            code,
            payload,
            (res) => {
                setSubjectInfo(res?.data);
                setInfoLoading(false);
            },
            (error) => {
                notify.error("Không thể lấy thông tin học phần");
            }
        ).then(() => {});
    };
    const getAllChapters = (payload = {}, code) => {
        setChapterLoading(true);
        getChaptersService(
            code,
            payload,
            (res) => {
                setAllChapters(res?.data);
                setChapterLoading(false);
            },
            (err) => {
                setChapterLoading(false);
                if (err?.response?.status === 404) {
                    notify.warning(
                        err.response.data.message ||
                        "No information in database"
                    );
                }
            }
        ).then(() => {});
    };
    return {
        allSubjects,
        getAllSubjects,
        tableLoading,
        subjectInfo,
        getSubjectByCode,
        infoLoading,
        allChapters,
        chapterLoading,
        getAllChapters,
        pagination,
    };
};

export default useSubjects;
