
CREATE TABLE IF NOT EXISTS public.message_formats (
                                                      id SERIAL PRIMARY KEY,
                                                      format_name VARCHAR(50) NOT NULL
    );


CREATE TABLE IF NOT EXISTS public.searadar_messages (
                                                        id SERIAL PRIMARY KEY,
                                                        message_type VARCHAR(50) NOT NULL,
    message_content TEXT NOT NULL,
    format_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (format_id) REFERENCES public.message_formats (id)
    );

INSERT INTO public.message_formats(
    id, format_name)
VALUES (1	,'mr-231'),(2,	'mr-231_3');

