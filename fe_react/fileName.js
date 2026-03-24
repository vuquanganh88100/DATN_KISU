const v8 = require('v8');

// Lấy thông tin về bộ nhớ heap hiện tại
const memoryUsage = v8.getHeapStatistics();
console.log('Memory Usage Statistics:', memoryUsage);
console.log('Heap Size Limit:', memoryUsage.heap_size_limit / (1024 * 1024), 'MB');
console.log('Total Heap Size:', memoryUsage.total_heap_size / (1024 * 1024), 'MB');
console.log('Used Heap Size:', memoryUsage.used_heap_size / (1024 * 1024), 'MB');