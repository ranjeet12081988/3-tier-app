from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime

with DAG(
    'test_pipeline',
    start_date=datetime(2026, 1, 1),
    schedule_interval=None,
    catchup=False
) as dag:

    task_1 = BashOperator(
        task_id='run_meltano_job',
        bash_command='echo "Simulating Meltano pipeline execution..."; sleep 2',
    )

    task_2 = BashOperator(
        task_id='verify_output',
        bash_command='echo "Pipeline verification successful"',
    )

    task_1 >> task_2
