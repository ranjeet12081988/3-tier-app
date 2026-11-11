from airflow import DAG
from airflow.providers.amazon.aws.sensors.s3 import S3KeySensor
from airflow.operators.bash import BashOperator
from datetime import datetime

with DAG(
    'minio_csv_trigger',
    start_date=datetime(2026, 1, 1),
    schedule_interval=None,  # Trigger manually or via external system
    catchup=False
) as dag:

    # Key: Monitor specified CSV file
    wait_for_csv = S3KeySensor(
        task_id='wait_for_csv_in_minio',
        bucket_name='my-bucket',           # Your MinIO bucket name
        bucket_key='uploads/data.csv',     # Path of the CSV file to monitor
        aws_conn_id='minio_conn',          # Your configured Airflow Connection ID
        poke_interval=30,                  # Check every 30 seconds
        timeout=3600,                      # Timeout after 1 hour
        mode='reschedule',                 # Release worker slot while waiting to save resources
        dag=dag
    )

    process_task = BashOperator(
        task_id='process_csv',
        bash_command='echo "CSV file detected, starting processing..."',
        dag=dag
    )

    wait_for_csv >> process_task