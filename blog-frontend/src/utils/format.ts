import dayjs from 'dayjs'

// 格式化时间
export const formatTime = (time: string | Date) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
} 