import React, { useState, useEffect } from 'react';
import { Space, Table, Button, Input, Modal, Form, message } from 'antd';
import ActionButton from '../../components/ActionButton/ActionButton';
import { useNavigate } from 'react-router-dom';
import { appPath } from '../../config/appPath';
import "./TopicContest.scss";
import SearchFilter from '../../components/SearchFilter/SearchFilter';
import { getTopicContests, addTopicContest } from '../../services/topicContestService';

export default function TopicContest() {
  const [allContests, setAllContests] = useState([]); // Lưu tất cả dữ liệu từ API
  const [displayedContests, setDisplayedContests] = useState([]); // Dữ liệu hiển thị theo trang
  const [totalContests, setTotalContests] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  // Function to map API data to component data structure
  const mapApiDataToContests = (apiData) => {
    return apiData.map((item, index) => ({
      key: item.id.toString(),
      contestId: item.id,
      contestName: item.name,
      ordinal: index + 1,
      submissionCount: item.numsOfProblems
    }));
  };

  const handleEditClick = (contestId) => {
    navigate(`${appPath.topicContestEdit}/${contestId}`);
  };

  const handleViewClick = (contestId) => {
    navigate(`${appPath.topicContestView}/${contestId}`);
  };

  const handleParticipantsClick = (contestId) => {
    navigate(`${appPath.topicContestParticipants}/${contestId}`);
  };

  const columns = [
    {
      title: "Số thứ tự",
      dataIndex: "ordinal",
      key: "ordinal",
      width: "15%",
      align: "center",
    },
    {
      title: "Tên chủ đề",
      dataIndex: "contestName",
      key: "contestName",
      width: "45%",
    },
    {
      title: "Số bài thi",
      dataIndex: "submissionCount",
      key: "submissionCount",
      width: "15%",
      align: "center",
    },
    {
      title: "Thao tác",
      key: "action",
      width: "25%",
      align: "center",
      render: (_, record) => (
        <Space size="middle" style={{ cursor: "pointer" }}>
          <ActionButton
            icon="edit"
            handleClick={() => handleEditClick(record.contestId)}
          />
          <ActionButton
            icon="detail"
            handleClick={() => handleViewClick(record.contestId)}
          />
          <ActionButton
            icon="statistic"
            handleClick={() => handleParticipantsClick(record.contestId)}
          />
        </Space>
      ),
    }
  ];

  useEffect(() => {
    fetchAllContests();
  }, []);

  useEffect(() => {
    // Cập nhật dữ liệu hiển thị khi allContests, page hoặc pageSize thay đổi
    updateDisplayedContests();
  }, [allContests, page, pageSize]);

  const fetchAllContests = () => {
    setLoading(true);
    getTopicContests(
      {}, // Không gửi param phân trang vì backend không hỗ trợ
      (response) => {
        console.log("Full API Response:", response);
        const apiData = Array.isArray(response) ? response : (response.data || []);
        const mappedData = mapApiDataToContests(apiData);
        setAllContests(mappedData);
        setTotalContests(apiData.length);
        setLoading(false);
      },
      (error) => {
        console.error("Error fetching topic contests:", error);
        message.error("Lỗi khi tải danh sách cuộc thi");
        setAllContests([]);
        setTotalContests(0);
        setLoading(false);
      }
    );
  };

  const updateDisplayedContests = () => {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const currentPageData = allContests.slice(startIndex, endIndex);
    setDisplayedContests(currentPageData);
  };

  const handlePageChange = (newPage, newPageSize) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const handleSearch = (value) => {
    if (!value) {
      updateDisplayedContests();
      return;
    }
    
    const filtered = allContests.filter(contest => 
      contest.contestId.toString().includes(value) || 
      contest.contestName.toLowerCase().includes(value.toLowerCase())
    );
    
    setDisplayedContests(filtered.slice(0, pageSize));
    setTotalContests(filtered.length);
    setPage(1);
  };

  const showModal = () => {
    setIsModalVisible(true);
  };

  const handleOk = () => {
    form.validateFields().then(values => {
      addTopicContest(
        { name: values.contestName },
        (response) => {
          message.success("Thêm cuộc thi thành công");
          setIsModalVisible(false);
          form.resetFields();
          fetchAllContests(); // Refresh toàn bộ danh sách sau khi thêm mới
        },
        (error) => {
          console.error("Error adding topic contest:", error);
          message.error("Lỗi khi thêm cuộc thi");
        }
      );
    }).catch(err => {
      console.error("Validation error:", err);
    });
  };

  const handleCancel = () => {
    setIsModalVisible(false);
    form.resetFields();
  };

  return (
    <div className='topic-contest'>
      <div className='topic-contest-header'>
        <p>Danh sách chủ đề contest</p>
      </div>
      <div className='search-filter-button'>
        <SearchFilter 
          placeholder="Nhập mã cuộc thi hoặc tên cuộc thi" 
          onSearch={handleSearch}
        />
        <Button type="primary" onClick={showModal} style={{ marginLeft: '10px' }}>
          Thêm cuộc thi mới
        </Button>
      </div>
      <Modal
        title="Thêm cuộc thi mới"
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        okText="Thêm"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="contestName"
            label="Tên cuộc thi"
            rules={[{ required: true, message: 'Vui lòng nhập tên cuộc thi!' }]}
          >
            <Input placeholder="Nhập tên cuộc thi" />
          </Form.Item>
        </Form>
      </Modal>
      <div className='topic-contest-wrapper'>
        <Table
          columns={columns}
          dataSource={displayedContests}
          rowKey="key"
          className="topic-contest-table"
          loading={loading}
          pagination={{
            current: page,
            pageSize: pageSize,
            total: totalContests,
            onChange: handlePageChange,
            showSizeChanger: true,
            pageSizeOptions: ['5', '10', '20'],
            showTotal: (total, range) => (
              <span>
                <strong>
                  {range[0]}-{range[1]}
                </strong>{" "}
                trong <strong>{total}</strong> cuộc thi
              </span>
            ),
          }}
        />
      </div>
    </div>
  );
}
