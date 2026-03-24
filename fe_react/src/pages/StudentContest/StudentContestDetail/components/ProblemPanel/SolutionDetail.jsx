import React, { useState, useEffect } from 'react';
import {
  Button,
  Avatar,
  Tag,
  message,
  Input,
  Empty,
  Divider,
  Spin,
} from 'antd';
import { ArrowLeftOutlined, SendOutlined } from '@ant-design/icons';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { createComment, getCommentsBySolution } from '../../../../../services/solutionServices';
import './SolutionDetail.scss';

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

const SolutionDetail = ({ solution, onBack, userInfo = {} }) => {
  const [commentText, setCommentText] = useState('');
  const [commentLoading, setCommentLoading] = useState(false);
  const [comments, setComments] = useState([]);
  const [loadingComments, setLoadingComments] = useState(false);

  // Fetch comments when solution changes
  useEffect(() => {
    if (solution?.solutionId) {
      fetchComments();
    }
  }, [solution?.solutionId]);

  const fetchComments = () => {
    setLoadingComments(true);
    getCommentsBySolution(
      solution.solutionId,
      {},
      (response) => {
        setComments(response || []);
        setLoadingComments(false);
      },
      (error) => {
        console.error('Failed to fetch comments:', error);
        setLoadingComments(false);
      }
    );
  };

  const handlePostComment = () => {
    if (!commentText.trim()) {
      message.warning('Please enter a comment');
      return;
    }

    setCommentLoading(true);

    createComment(
      {
        solutionId: solution.solutionId,
        content: commentText,
      },
      (response) => {
        message.success('Comment posted successfully!');

        // Add new comment locally
        const newComment = {
          id: Date.now(),
          solutionId: solution.solutionId,
          content: commentText,
          createdBy: userInfo?.userId || 'Unknown',
          createdAt: new Date().toISOString(),
        };

        setComments([...comments, newComment]);
        setCommentText('');
        setCommentLoading(false);
      },
      (error) => {
        message.error('Failed to post comment');
        setCommentLoading(false);
      }
    );
  };

  if (!solution) return null;

  return (
    <div className="solution-detail-container">
      <div className="detail-header">
        <Button
          type="text"
          icon={<ArrowLeftOutlined />}
          onClick={onBack}
          className="back-button"
        >
          Back to Solutions
        </Button>
      </div>

      <div className="detail-content">
        <div className="detail-title-section">
          <h2>{solution.title}</h2>
          <Tag color="blue">
            {LANGUAGE_MAP[solution.languageId] || `Language ${solution.languageId}`}
          </Tag>
        </div>

        <div className="detail-metadata">
          <div className="meta-item">
            <strong>Time Complexity:</strong>
            <span>{solution.timeComplex || 'N/A'}</span>
          </div>
          <div className="meta-item">
            <strong>Space Complexity:</strong>
            <span>{solution.spaceComplex || 'N/A'}</span>
          </div>
        </div>

        <div className="detail-author">
          <Avatar size={40} style={{ backgroundColor: '#87d068' }}>
            {solution.createdBy || 'U'}
          </Avatar>
          <div className="author-details">
            <strong>User #{solution.createdBy}</strong>
            <small>{new Date(solution.createdAt).toLocaleDateString()}</small>
          </div>
        </div>

        <Divider />

        <div className="detail-content-section">
          <h3>Solution</h3>
          <div className="editor-wrapper">
            <ReactQuill
              value={solution.content || ''}
              readOnly={true}
              theme="snow"
              modules={{
                toolbar: false,
              }}
            />
          </div>
        </div>

        <Divider />

        <div className="detail-comments">
          <h3>Comments ({comments.length})</h3>

          <Spin spinning={loadingComments}>
            {comments.length > 0 ? (
              <div className="comments-list">
                {comments.map((comment) => (
                  <div key={comment.id} className="comment-item">
                    <div className="comment-header">
                      <Avatar size={32} style={{ backgroundColor: '#1890ff' }}>
                        {comment.createdBy || 'U'}
                      </Avatar>
                      <div className="comment-info">
                        <strong>User #{comment.createdBy}</strong>
                        <small>{new Date(comment.createdAt).toLocaleDateString()}</small>
                      </div>
                    </div>
                    <p className="comment-content">{comment.content}</p>
                  </div>
                ))}
              </div>
            ) : (
              <Empty description="No comments yet" style={{ margin: '20px 0' }} />
            )}
          </Spin>

          <Divider />

          <div className="comment-form">
            <h4>Add a Comment</h4>
            <Input.TextArea
              rows={3}
              placeholder="Share your thoughts..."
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              disabled={commentLoading}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handlePostComment}
              loading={commentLoading}
              style={{ marginTop: '12px' }}
            >
              Post Comment
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SolutionDetail;