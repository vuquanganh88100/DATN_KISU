import React, { useState, useRef, useEffect } from 'react';
import { Drawer, Button, Input, Space, Empty, Avatar, Tooltip } from 'antd';
import {
  SendOutlined,
  RobotOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import { sendMessagatoChatbot } from '../../../../../services/studentContestService';
import './ChatAssistantDrawer.scss';

const VALID_COMMANDS = ['/hint', '/debug', '/explain'];

const ChatAssistantDrawer = ({
  isOpen = false,
  onClose = () => {},
  problem = {},
  submissionId = null,
  onSubmissionUpdate = () => {},
}) => {
  const [messages, setMessages] = useState([
    {
      id: 1,
      type: 'assistant',
      content: 'Hi! I\'m your coding assistant. I can help you with:\n• /hint - Get hints about the problem\n• /debug - Analyze errors in your code\n• /explain - Understand algorithms and optimizations',
      timestamp: new Date(),
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [currentSubmissionId, setCurrentSubmissionId] = useState(submissionId);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    setCurrentSubmissionId(submissionId);
  }, [submissionId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const validateCommand = (text) => {
    const trimmed = text.trim();
    const command = trimmed.split(' ')[0].toLowerCase();
    return VALID_COMMANDS.includes(command);
  };

  const handleSendMessage = async () => {
    if (!inputValue.trim()) return;

    if (!validateCommand(inputValue)) {
      const errorMessage = {
        id: messages.length + 1,
        type: 'error',
        content: 'Message must start with: /hint, /debug, or /explain',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
      return;
    }

    if (!problem?.id) {
      const errorMessage = {
        id: messages.length + 1,
        type: 'error',
        content: 'Problem information is missing',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
      return;
    }

    // Check if /debug command requires submissionId
    const command = inputValue.split(' ')[0].toLowerCase();
    if (command === '/debug' && !currentSubmissionId) {
      const errorMessage = {
        id: messages.length + 1,
        type: 'error',
        content: 'Please submit your code first before using /debug',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
      return;
    }

    // Add user message
    const userMessage = {
      id: messages.length + 1,
      type: 'user',
      content: inputValue,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue('');
    setIsLoading(true);

    try {
      sendMessagatoChatbot(
        {
          problemId: problem.id,
          submissionId: currentSubmissionId,
          message: inputValue,
        },
        (response) => {
          const assistantMessage = {
            id: messages.length + 2,
            type: 'assistant',
            content: response?.answer || response?.data?.answer || response?.data?.response || response?.message || 'Unable to get response',
            timestamp: new Date(),
          };

          setMessages((prev) => [...prev, assistantMessage]);

          // Update submission ID if returned
          if (response?.data?.submissionId) {
            setCurrentSubmissionId(response.data.submissionId);
            onSubmissionUpdate(response.data.submissionId);
          }

          setIsLoading(false);
        },
        (error) => {
          const errorMessage = {
            id: messages.length + 2,
            type: 'error',
            content: error?.response?.data?.message || error?.message || 'Failed to get response from assistant',
            timestamp: new Date(),
          };
          setMessages((prev) => [...prev, errorMessage]);
          setIsLoading(false);
        }
      );
    } catch (error) {
      const errorMessage = {
        id: messages.length + 2,
        type: 'error',
        content: 'An error occurred while sending message',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
      setIsLoading(false);
    }
  };

  // Handle Enter key to send
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <Drawer
      title={
        <Space>
          <RobotOutlined style={{ fontSize: 20, color: '#3b82f6' }} />
          <span>Code Assistant</span>
        </Space>
      }
      placement="right"
      onClose={onClose}
      open={isOpen}
      width={400}
      className="chat-assistant-drawer"
      destroyOnClose
    >
      {/* Messages Container */}
      <div className="chat-messages">
        {messages.length === 0 ? (
          <Empty
            description="Start a conversation"
            style={{ marginTop: 48 }}
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          />
        ) : (
          messages.map((msg) => (
            <div key={msg.id} className={`message message-${msg.type}`}>
              <div className="message-avatar">
                {msg.type === 'assistant' ? (
                  <RobotOutlined className="avatar-icon" />
                ) : msg.type === 'error' ? (
                  <ExclamationCircleOutlined className="avatar-icon error-icon" />
                ) : (
                  <Avatar size="small" style={{ backgroundColor: '#3b82f6' }}>
                    U
                  </Avatar>
                )}
              </div>
              <div className="message-content">
                <div className={`message-bubble ${msg.type === 'error' ? 'error' : ''}`}>
                  {msg.content}
                </div>
                <span className="message-time">
                  {msg.timestamp.toLocaleTimeString([], {
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </span>
              </div>
            </div>
          ))
        )}

        {isLoading && (
          <div className="message message-assistant">
            <div className="message-avatar">
              <RobotOutlined className="avatar-icon" />
            </div>
            <div className="message-content">
              <div className="message-bubble loading">
                <span className="typing-dot" />
                <span className="typing-dot" />
                <span className="typing-dot" />
              </div>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="chat-input-area">
        <div className="command-hint">
          <span className="hint-text">Start with: /hint, /debug, or /explain</span>
        </div>
        <Space.Compact style={{ width: '100%' }}>
          <Input.TextArea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Ask a question about this problem..."
            rows={2}
            disabled={isLoading}
            className="chat-input"
          />
          <Tooltip title="Send message (Enter or Shift+Enter for new line)">
            <Button
              icon={<SendOutlined />}
              type="primary"
              onClick={handleSendMessage}
              loading={isLoading}
              disabled={!inputValue.trim() || isLoading}
            />
          </Tooltip>
        </Space.Compact>
      </div>

      {/* Submission Status */}
      {currentSubmissionId && (
        <div className="chat-status">
          <p className="status-label">Submission ID: <strong>{currentSubmissionId}</strong></p>
        </div>
      )}
    </Drawer>
  );
};

export default ChatAssistantDrawer;
