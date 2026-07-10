import { createError } from 'h3';

export const getJwtSecret = () => {
  const secret = process.env.JWT_SECRET?.trim();

  if (!secret || secret.length < 32) {
    throw createError({
      statusCode: 500,
      statusMessage: 'JWT_SECRET deve ser configurado com pelo menos 32 caracteres.'
    });
  }

  return secret;
};
