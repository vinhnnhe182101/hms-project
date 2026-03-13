// src/hooks/useHousekeepingTasks.js
import { useState, useEffect, useCallback } from 'react';
import { housekeepingApi } from '../apis/housekeepingApi';
import { notifications } from '@mantine/notifications';

export const useHousekeepingTasks = () => {
    const [tasks, setTasks] = useState([]);
    const [todayTasks, setTodayTasks] = useState([]);
    const [counts, setCounts] = useState({
        scheduled: 0,
        inProgress: 0,
        completed: 0,
        total: 0
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [actionLoading, setActionLoading] = useState(false);

    const fetchTasks = useCallback(async () => {
        try {
            setLoading(true);
            const response = await housekeepingApi.getMyTasks();
            setTasks(response.data || []);
        } catch (err) {
            setError(err.message);
            notifications.show({
                title: 'Error',
                message: 'Cannot load tasks',
                color: 'red'
            });
        } finally {
            setLoading(false);
        }
    }, []);

    const fetchTodayTasks = useCallback(async () => {
        try {
            const response = await housekeepingApi.getTodayTasks();
            setTodayTasks(response.data || []);
        } catch (err) {
            console.error('Error fetching today tasks:', err);
        }
    }, []);

    const fetchTaskCounts = useCallback(async () => {
        try {
            const response = await housekeepingApi.getTaskCounts();
            setCounts(response.data || {
                scheduled: 0,
                inProgress: 0,
                completed: 0,
                total: 0
            });
        } catch (err) {
            console.error('Error fetching counts:', err);
        }
    }, []);

    // Action: Start Task
    const startTask = useCallback(async (taskId) => {
        setActionLoading(true);
        try {
            const response = await housekeepingApi.startTask(taskId);

            notifications.show({
                title: 'Success',
                message: 'Task started successfully',
                color: 'green'
            });

            // Refresh data
            await Promise.all([
                fetchTasks(),
                fetchTodayTasks(),
                fetchTaskCounts()
            ]);

            return { success: true, data: response.data };
        } catch (error) {
            const message = error.response?.data?.message || 'Failed to start task';
            notifications.show({
                title: 'Error',
                message,
                color: 'red'
            });
            return { success: false, error: message };
        } finally {
            setActionLoading(false);
        }
    }, [fetchTasks, fetchTodayTasks, fetchTaskCounts]);

    // Action: Complete Task
    const completeTask = useCallback(async (taskId) => {
        setActionLoading(true);
        try {
            const response = await housekeepingApi.completeTask(taskId);

            notifications.show({
                title: 'Success',
                message: 'Task completed successfully',
                color: 'green'
            });

            // Refresh data
            await Promise.all([
                fetchTasks(),
                fetchTodayTasks(),
                fetchTaskCounts()
            ]);

            return { success: true, data: response.data };
        } catch (error) {
            const message = error.response?.data?.message || 'Failed to complete task';
            notifications.show({
                title: 'Error',
                message,
                color: 'red'
            });
            return { success: false, error: message };
        } finally {
            setActionLoading(false);
        }
    }, [fetchTasks, fetchTodayTasks, fetchTaskCounts]);

    useEffect(() => {
        const loadData = async () => {
            await Promise.all([
                fetchTasks(),
                fetchTodayTasks(),
                fetchTaskCounts()
            ]);
        };

        loadData();
    }, [fetchTasks, fetchTodayTasks, fetchTaskCounts]);

    return {
        tasks,
        todayTasks,
        counts,
        loading,
        error,
        actionLoading,
        fetchTasks,
        fetchTodayTasks,
        fetchTaskCounts,
        startTask,
        completeTask
    };
};