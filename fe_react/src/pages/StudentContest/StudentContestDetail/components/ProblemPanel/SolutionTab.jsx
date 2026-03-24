import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Select,
  Space,
  message,
  Modal,
  Button,
  Card,
} from 'antd';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import {
  createSolution,
  getSolutionsByProblem,
} from '../../../../../services/solutionServices';
import SolutionList from './SolutionList';
import SolutionDetail from './SolutionDetail';
import './SolutionTab.scss';

const LANGUAGE_MAP = {
  62: 'Java',
  71: 'Python',
  72: 'JavaScript',
  73: 'TypeScript',
  74: 'C++',
  75: 'C',
  76: 'C#',
  77: 'Go',
  78: 'Rust',
};

const SolutionTab = ({ problemId, userInfo = {} }) => {
  const [solutions, setSolutions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submittingForm, setSubmittingForm] = useState(false);
  const [form] = Form.useForm();
  const [showPostSolution, setShowPostSolution] = useState(false);
  const [selectedSolution, setSelectedSolution] = useState(null);
  const [contentValue, setContentValue] = useState('');

  // Fetch solutions on component mount
  useEffect(() => {
    fetchSolutions();
  }, [problemId]);

  const fetchSolutions = () => {
    setLoading(true);
    getSolutionsByProblem(
      problemId,
      {},
      (response) => {
        setSolutions(response || []);
        setLoading(false);
      },
      (error) => {
        message.error('Failed to load solutions');
        setLoading(false);
      }
    );
  };

  const handlePostSolution = async (values) => {
    setSubmittingForm(true);
    createSolution(
      {
        ...values,
        content: contentValue,
        problemId: problemId,
      },
      (response) => {
        message.success('Solution posted successfully!');
        form.resetFields();
        setContentValue('');
        setShowPostSolution(false);
        fetchSolutions();
        setSubmittingForm(false);
      },
      (error) => {
        message.error('Failed to post solution');
        setSubmittingForm(false);
      }
    );
  };

  const handleOpenDetail = (solution) => {
    setSelectedSolution(solution);
  };

  const SolutionCard = ({ solution }) => {
    return (
      <Card
        className="solution-card"
        key={solution.solutionId}
        onClick={() => handleOpenDetail(solution)}
        hoverable
      >
        <div className="solution-header">
          <div className="solution-title-section">
            <h3 className="solution-title">{solution.title}</h3>
            <span className="language-tag">
              {LANGUAGE_MAP[solution.languageId] || `Language ${solution.languageId}`}
            </span>
          </div>
        </div>

        <div className="solution-meta-compact">
          <span className="complexity">
            <strong>Time:</strong> {solution.timeComplex || 'N/A'}
          </span>
          <span className="complexity">
            <strong>Space:</strong> {solution.spaceComplex || 'N/A'}
          </span>
        </div>

        <div className="solution-author">
          <span className="author-info">
            User #{solution.createdBy}
            <br />
            <small>{new Date(solution.createdAt).toLocaleDateString()}</small>
          </span>
        </div>

        <div className="solution-actions-compact">
          <Button
            type="text"
            size="small"
            icon={<span>👍</span>}
          >
            Like
          </Button>
          <Button
            type="text"
            size="small"
            icon={<span>💬</span>}
          >
            {(solution.comments || []).length} Comments
          </Button>
        </div>
      </Card>
    );
  };

  const SolutionDetailView = () => {
    return (
      <SolutionDetail 
        solution={selectedSolution} 
        onBack={() => setSelectedSolution(null)}
        userInfo={userInfo}
      />
    );
  };

  return (
    <div className="solution-tab">
      {selectedSolution ? (
        <div className="solution-detail-view">
          <SolutionDetailView />
        </div>
      ) : (
        <>
          <div className="solution-toolbar">
            <h2>Solutions</h2>
            <Button
              type="primary"
              size="large"
              onClick={() => setShowPostSolution(true)}
            >
              Post Solution
            </Button>
          </div>

          <Modal
            title="Post a Solution"
            open={showPostSolution}
            onCancel={() => {
              setShowPostSolution(false);
              form.resetFields();
            }}
            footer={null}
            width={700}
          >
            <Form
              form={form}
              layout="vertical"
              onFinish={handlePostSolution}
            >
              <Form.Item
                name="title"
                label="Solution Title"
                rules={[{ required: true, message: 'Please enter solution title' }]}
              >
                <Input placeholder="e.g., O(n) simple approach with loop" />
              </Form.Item>

              <Form.Item
                name="languageId"
                label="Programming Language"
                rules={[{ required: true, message: 'Please select a language' }]}
              >
                <Select placeholder="Select programming language">
                  {Object.entries(LANGUAGE_MAP).map(([id, name]) => (
                    <Select.Option key={id} value={parseInt(id)}>
                      {name}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>

              <div>
                <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>
                  Solution Content <span style={{ color: 'red' }}>*</span>
                </label>
                <div style={{ border: '1px solid #d9d9d9', borderRadius: '4px' }}>
                  <ReactQuill
                    value={contentValue}
                    onChange={setContentValue}
                    modules={{
                      toolbar: [
                        [{ 'header': [1, 2, 3, false] }],
                        ['bold', 'italic', 'underline', 'strike'],
                        ['blockquote', 'code-block'],
                        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                        ['link'],
                        ['clean']
                      ]
                    }}
                    placeholder="Write your solution explanation and code here..."
                    style={{ height: '400px' }}
                  />
                </div>
                {!contentValue && submittingForm && (
                  <div style={{ color: '#ff4d4f', fontSize: '12px', marginTop: '4px' }}>
                    Please enter solution content
                  </div>
                )}
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <Form.Item
                  name="timeComplex"
                  label="Time Complexity"
                  rules={[{ required: true, message: 'Please enter time complexity' }]}
                >
                  <Input placeholder="e.g., O(n)" />
                </Form.Item>

                <Form.Item
                  name="spaceComplex"
                  label="Space Complexity"
                  rules={[{ required: true, message: 'Please enter space complexity' }]}
                >
                  <Input placeholder="e.g., O(1)" />
                </Form.Item>
              </div>

              <Form.Item>
                <Space>
                  <Button
                    type="primary"
                    htmlType="submit"
                    loading={submittingForm}
                  >
                    Post Solution
                  </Button>
                  <Button onClick={() => setShowPostSolution(false)}>Cancel</Button>
                </Space>
              </Form.Item>
            </Form>
          </Modal>

          <div className="solutions-list">
            {loading ? (
              <div style={{ textAlign: 'center', padding: '40px' }}>Loading...</div>
            ) : solutions && solutions.length > 0 ? (
              solutions.map((solution) => (
                <SolutionCard key={solution.solutionId} solution={solution} />
              ))
            ) : (
              <div style={{ textAlign: 'center', padding: '40px' }}>
                No solutions yet. Be the first to share!
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default SolutionTab;